package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.data.remote.SoilResponse
import kotlinx.coroutines.flow.Flow

interface SoilRepository {
    fun getSoilData(lat: Double, lon: Double): Flow<SoilResponse>
}
