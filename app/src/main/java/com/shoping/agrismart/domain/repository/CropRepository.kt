package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.Crop
import kotlinx.coroutines.flow.Flow

interface CropRepository {
    fun getCrops(): Flow<List<Crop>>
    fun getRecommendedCrops(
        location: String,
        soilType: String,
        season: String,
        waterSource: String,
        budget: Float
    ): Flow<List<Crop>>
    
    fun getAiCropAdvice(
        location: String,
        soilType: String,
        season: String,
        waterSource: String,
        budget: Float,
        recommendedCrops: List<Crop>
    ): Flow<String>
}
