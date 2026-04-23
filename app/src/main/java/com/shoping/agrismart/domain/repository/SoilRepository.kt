package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.data.remote.SoilResponse
import kotlinx.coroutines.flow.Flow

interface SoilRepository {
    fun getSoilData(lat: Double, lon: Double): Flow<SoilResponse>
}
