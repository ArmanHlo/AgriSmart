package com.shoping.agrismartapp.data.repository

import com.shoping.agrismartapp.data.remote.SoilApiService
import com.shoping.agrismartapp.data.remote.SoilResponse
import com.shoping.agrismartapp.domain.repository.SoilRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SoilRepositoryImpl @Inject constructor(
    private val apiService: SoilApiService
) : SoilRepository {
    override fun getSoilData(lat: Double, lon: Double): Flow<SoilResponse> = flow {
        val response = apiService.getSoilData(lat, lon)
        emit(response)
    }.flowOn(Dispatchers.IO)
}
