package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.domain.model.Comment
import com.shoping.agrismartapp.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun createPost(content: String, authorName: String, topic: String)
    suspend fun likePost(post: Post)
    suspend fun addComment(postId: String, content: String, authorName: String)
    fun getComments(postId: String): Flow<List<Comment>>
}
