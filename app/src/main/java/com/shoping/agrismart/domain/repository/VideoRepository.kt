package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getVideos(): Flow<List<Video>>
    fun getVideosByCategory(category: String): Flow<List<Video>>
}
