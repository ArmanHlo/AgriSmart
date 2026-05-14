package com.shoping.agrismart.data.repository

import com.shoping.agrismart.data.local.dao.WeatherDao
import com.shoping.agrismart.data.local.entity.WeatherEntity
import com.shoping.agrismart.data.remote.WeatherApiService
import com.shoping.agrismart.domain.repository.WeatherRepository
import com.shoping.agrismart.data.remote.WeatherResponse
import com.shoping.agrismart.data.remote.MainData
import com.shoping.agrismart.data.remote.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> = channelFlow {
        // 1. Emit cached data first
        weatherDao.getCachedWeather().firstOrNull()?.let { entity ->
            send(WeatherResponse(
                main = MainData(entity.temperature, entity.temperature, entity.humidity),
                weather = listOf(WeatherData(entity.description, entity.icon)),
                name = entity.cityName
            ))
        }

        // 2. Fetch fresh data from API
        try {
            val response = apiService.getCurrentWeather(lat, lon)
            val mappedResponse = WeatherResponse(
                main = MainData(
                    temp = response.currentWeather.temp,
                    feels_like = response.currentWeather.temp,
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

            // 3. Update cache
            weatherDao.clearCache()
            weatherDao.cacheWeather(WeatherEntity(
                id = 1,
                lat = lat,
                lon = lon,
                cityName = mappedResponse.name,
                temperature = mappedResponse.main.temp,
                humidity = mappedResponse.main.humidity,
                description = mappedResponse.weather.first().description,
                icon = mappedResponse.weather.first().icon,
                timestamp = System.currentTimeMillis()
            ))

            send(mappedResponse)
        } catch (e: Exception) {
            // Silently fail if no network
        }
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
