package com.shoping.agrismart.data.repository

import com.shoping.agrismart.data.remote.WeatherApiService
import com.shoping.agrismart.domain.repository.WeatherRepository
import com.shoping.agrismart.data.remote.WeatherResponse
import com.shoping.agrismart.data.remote.MainData
import com.shoping.agrismart.data.remote.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> = flow {
        // Now using Open-Meteo (Free, no API key required)
        val response = apiService.getCurrentWeather(lat, lon)
        
        // Map Open-Meteo response to our existing WeatherResponse model to avoid breaking UI
        val mappedResponse = WeatherResponse(
            main = MainData(
                temp = response.currentWeather.temp,
                feels_like = response.currentWeather.temp, // Approximation
                humidity = response.hourly.humidity.firstOrNull()?.toInt() ?: 0
            ),
            weather = listOf(
                WeatherData(
                    description = mapWeatherCode(response.currentWeather.conditionCode),
                    icon = "01d"
                )
            ),
            name = "Your Farm"
        )
        emit(mappedResponse)
    }.flowOn(Dispatchers.IO)

    private fun mapWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Mainly clear, partly cloudy, and overcast"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow"
            95 -> "Thunderstorm"
            else -> "Partly Cloudy"
        }
    }
}
