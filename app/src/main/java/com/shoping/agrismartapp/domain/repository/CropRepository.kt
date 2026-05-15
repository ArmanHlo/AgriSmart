package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.domain.model.Crop
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
