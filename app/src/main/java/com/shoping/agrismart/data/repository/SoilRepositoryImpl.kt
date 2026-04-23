package com.shoping.agrismart.data.repository

import com.shoping.agrismart.data.remote.SoilApiService
import com.shoping.agrismart.data.remote.SoilResponse
import com.shoping.agrismart.domain.repository.SoilRepository
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
