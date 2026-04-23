package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.Crop
import kotlinx.coroutines.flow.Flow

interface CropRepository {
    fun getCrops(): Flow<List<Crop>>
    fun getRecommendedCrops(
        location: String,
        soilType: String,
        season: String
    ): Flow<List<Crop>>
}
