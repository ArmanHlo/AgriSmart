package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.data.remote.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse>
}
