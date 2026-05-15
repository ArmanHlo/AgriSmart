package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.data.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse>
}
