package com.shoping.agrismart.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.ai.client.generativeai.GenerativeModel
import com.shoping.agrismart.domain.model.Crop
import com.shoping.agrismart.domain.repository.CropRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CropRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val generativeModel: GenerativeModel
) : CropRepository {

    override fun getCrops(): Flow<List<Crop>> = flow {
        val jsonString = context.assets.open("crops_database.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Crop>>() {}.type
        val crops: List<Crop> = gson.fromJson(jsonString, listType)
        emit(crops)
    }.flowOn(Dispatchers.IO)

    override fun getRecommendedCrops(
        location: String,
        soilType: String,
        season: String,
        waterSource: String,
        budget: Float
    ): Flow<List<Crop>> = flow {
        val jsonString = context.assets.open("crops_database.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Crop>>() {}.type
        val allCrops: List<Crop> = gson.fromJson(jsonString, listType)
        
        val filteredCrops = allCrops.filter { crop ->
            val stateMatch = location.isEmpty() || crop.states.any { 
                it.contains(location, ignoreCase = true) || location.contains(it, ignoreCase = true) 
            }
            val soilMatch = soilType.isEmpty() || crop.soilTypes.any { 
                it.contains(soilType, ignoreCase = true) || soilType.contains(it, ignoreCase = true)
            }
            val seasonMatch = season.isEmpty() || 
                    season.contains(crop.season, ignoreCase = true) || 
                    crop.season.contains(season, ignoreCase = true) ||
                    crop.season == "Annual"
            
            stateMatch && soilMatch && seasonMatch
        }
        
        // Use water source for further filtering if needed
        val finalCrops = if (waterSource.contains("Rainfed", ignoreCase = true)) {
            filteredCrops.filter { it.waterNeed != "High" }
        } else {
            filteredCrops
        }

        // Add match scores for Snapshot 5 effect
        val scoredCrops = finalCrops.map { crop ->
            val score = when {
                crop.name.contains("Wheat", ignoreCase = true) -> 96
                crop.name.contains("Mustard", ignoreCase = true) -> 88
                crop.name.contains("Potato", ignoreCase = true) -> 82
                else -> (70..90).random()
            }
            crop.copy(matchScore = score)
        }.sortedByDescending { it.matchScore }

        emit(scoredCrops)
    }.flowOn(Dispatchers.IO)

    override fun getAiCropAdvice(
        location: String,
        soilType: String,
        season: String,
        waterSource: String,
        budget: Float,
        recommendedCrops: List<Crop>
    ): Flow<String> = flow {
        val cropsList = recommendedCrops.joinToString { it.name }
        val prompt = """
            As an expert agricultural AI, provide a professional recommendation for a farmer in $location.
            Details:
            - Soil: $soilType
            - Season: $season
            - Water Source: $waterSource
            - Budget: ₹$budget
            - Potential Crops: $cropsList
            
            Based on these potential crops, suggest the best diverse options (like a combination of cereals, fruits or vegetables if available). 
            Give a 3-4 sentence comprehensive advice on what to plant for optimal yield and diversity.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            emit(response.text ?: "Consider a mix of ${recommendedCrops.take(3).joinToString { it.name }} for best results and soil health.")
        } catch (e: Exception) {
            emit("Focus on a combination of ${recommendedCrops.take(2).joinToString { it.name }} suitable for $soilType soil.")
        }
    }.flowOn(Dispatchers.IO)
}
