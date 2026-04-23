package com.shoping.agrismart.data.repository

import com.shoping.agrismart.domain.model.Video
import com.shoping.agrismart.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor() : VideoRepository {
    override fun getVideos(): Flow<List<Video>> = flow {
        val dummyVideos = listOf(
            Video(
                id = "1",
                title = "Modern Wheat Farming Techniques",
                thumbnailUrl = "https://img.youtube.com/vi/XmD29vE_zO8/0.jpg",
                videoUrl = "https://www.youtube.com/watch?v=XmD29vE_zO8",
                category = "Crops",
                duration = "10:24"
            ),
            Video(
                id = "2",
                title = "Organic Pest Control Guide",
                thumbnailUrl = "https://img.youtube.com/vi/gv7_M9S7O74/0.jpg",
                videoUrl = "https://www.youtube.com/watch?v=gv7_M9S7O74",
                category = "Organic",
                duration = "15:45"
            ),
            Video(
                id = "3",
                title = "Smart Irrigation Systems",
                thumbnailUrl = "https://img.youtube.com/vi/5S5P7p1p_Yg/0.jpg",
                videoUrl = "https://www.youtube.com/watch?v=5S5P7p1p_Yg",
                category = "Irrigation",
                duration = "08:12"
            )
        )
        emit(dummyVideos)
    }

    override fun getVideosByCategory(category: String): Flow<List<Video>> = flow {
        val allVideos = listOf(
            Video("1", "Modern Wheat Farming", "", "", "Crops", "10:24"),
            Video("2", "Organic Pest Control", "", "", "Organic", "15:45")
        )
        emit(allVideos.filter { it.category == category })
    }
}
