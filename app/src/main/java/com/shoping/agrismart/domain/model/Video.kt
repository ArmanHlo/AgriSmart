package com.shoping.agrismart.domain.model

data class Video(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val category: String,
    val duration: String
)
