package com.shoping.agrismart.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val gson: Gson
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
        season: String
    ): Flow<List<Crop>> = flow {
        val jsonString = context.assets.open("crops_database.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Crop>>() {}.type
        val allCrops: List<Crop> = gson.fromJson(jsonString, listType)
        
        val filteredCrops = allCrops.filter { crop ->
            (location.isEmpty() || crop.states.any { it.contains(location, ignoreCase = true) }) &&
            (soilType.isEmpty() || crop.soilTypes.any { it.contains(soilType, ignoreCase = true) }) &&
            (season.isEmpty() || crop.season.contains(season, ignoreCase = true) || crop.season == "Annual")
        }
        emit(filteredCrops)
    }.flowOn(Dispatchers.IO)
}
