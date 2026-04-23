package com.shoping.agrismart.domain.model

import java.util.Date

data class Post(
    val id: String = "",
    val authorName: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val commentCount: Int = 0,
    val topic: String = "General"
)
