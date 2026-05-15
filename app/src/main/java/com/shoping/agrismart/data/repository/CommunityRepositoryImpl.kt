package com.shoping.agrismart.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shoping.agrismart.domain.model.Comment
import com.shoping.agrismart.domain.model.Post
import com.shoping.agrismart.domain.repository.CommunityRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommunityRepository {

    private val mockPosts = MutableStateFlow<List<Post>>(emptyList())
    private val mockComments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())

    override fun getPosts(): Flow<List<Post>> = combine(
        callbackFlow<List<Post>> {
            val subscription = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList()) 
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
                    trySend(posts)
                }
            awaitClose { subscription.remove() }
        }.onStart { emit(emptyList()) }, // Don't block the UI while waiting for Firebase
        mockPosts
    ) { firebasePosts, manualPosts ->
        (manualPosts + firebasePosts)
            .distinctBy { it.id }
            .sortedByDescending { it.timestamp ?: Date(0) }
    }

    override suspend fun createPost(content: String, authorName: String, topic: String) {
        val post = Post(
            id = UUID.randomUUID().toString(),
            authorName = authorName,
            content = content,
            topic = topic,
            timestamp = Date(),
            commentCount = 0,
            likes = 0
        )
        
        // Immediate local update
        mockPosts.value = listOf(post) + mockPosts.value
        
        try {
            firestore.collection("posts").document(post.id).set(post).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun likePost(postId: String) {
        val current = mockPosts.value.toMutableList()
        val index = current.indexOfFirst { it.id == postId }
        
        if (index != -1) {
            val post = current[index]
            val wasLiked = post.liked
            val newLikes = if (wasLiked) post.likes - 1 else post.likes + 1
            current[index] = post.copy(likes = newLikes.coerceAtLeast(0), liked = !wasLiked)
            mockPosts.value = current
        }

        try {
            val docRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLikes = snapshot.getLong("likes") ?: 0
                val isCurrentlyLiked = index != -1 && current[index].liked
                val finalLikes = if (isCurrentlyLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)
                transaction.update(docRef, "likes", finalLikes)
                transaction.update(docRef, "liked", isCurrentlyLiked)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addComment(postId: String, content: String, authorName: String) {
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            authorName = authorName,
            content = content,
            timestamp = Date()
        )
        
        // Update local mock state immediately
        val currentMocks = mockComments.value.toMutableMap()
        currentMocks[postId] = (currentMocks[postId] ?: emptyList()) + comment
        mockComments.value = currentMocks

        val currentPosts = mockPosts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            currentPosts[postIndex] = currentPosts[postIndex].copy(
                commentCount = currentPosts[postIndex].commentCount + 1
            )
            mockPosts.value = currentPosts
        }

        try {
            val postRef = firestore.collection("posts").document(postId)
            val commentRef = postRef.collection("comments").document(comment.id)
            
            firestore.runTransaction { transaction ->
                transaction.set(commentRef, comment)
                transaction.update(postRef, "commentCount", FieldValue.increment(1))
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getComments(postId: String): Flow<List<Comment>> = combine(
        callbackFlow<List<Comment>> {
            val subscription = firestore.collection("posts").document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val comments = snapshot?.toObjects(Comment::class.java) ?: emptyList()
                    trySend(comments)
                }
            awaitClose { subscription.remove() }
        }.onStart { emit(emptyList()) },
        mockComments.map { it[postId] ?: emptyList() }
    ) { firebaseComments, localComments ->
        (localComments + firebaseComments)
            .distinctBy { it.id }
            .sortedBy { it.timestamp ?: Date(0) }
    }
}
