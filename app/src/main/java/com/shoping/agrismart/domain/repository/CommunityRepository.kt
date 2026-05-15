package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.Comment
import com.shoping.agrismart.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun createPost(content: String, authorName: String, topic: String)
    suspend fun likePost(post: Post)
    suspend fun addComment(postId: String, content: String, authorName: String)
    fun getComments(postId: String): Flow<List<Comment>>
}
