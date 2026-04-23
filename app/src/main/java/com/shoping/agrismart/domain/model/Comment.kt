package com.shoping.agrismart.domain.model

import java.util.Date

data class Comment(
    val id: String = "",
    val authorName: String = "",
    val content: String = "",
    val timestamp: Date = Date()
)
