package com.shoping.agrismart.data.repository

import com.shoping.agrismart.data.local.dao.WeatherDao
import com.shoping.agrismart.data.local.entity.WeatherEntity
import com.shoping.agrismart.data.remote.WeatherApiService
import com.shoping.agrismart.domain.repository.WeatherRepository
import com.shoping.agrismart.data.remote.WeatherResponse
import com.shoping.agrismart.data.remote.MainData
import com.shoping.agrismart.data.remote.WeatherData
import com.shoping.agrismart.data.remote.WindData
import com.shoping.agrismart.data.remote.SoilData
import com.shoping.agrismart.data.remote.ForecastData
import com.shoping.agrismart.data.remote.SmartAdvisory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> = channelFlow {
        // 1. Observe the database continuously
        val dbJob = launch {
            weatherDao.getCachedWeather()
                .filterNotNull()
                .collect { entity ->
                    send(WeatherResponse(
                        main = MainData(
                            temp = entity.temperature,
                            feels_like = entity.temperature,
                            humidity = entity.humidity,
                            evapotranspiration = entity.evapotranspiration
                        ),
                        weather = listOf(WeatherData(entity.description, entity.icon)),
                        name = entity.cityName,
                        wind = WindData(entity.windSpeed),
                        uvIndex = entity.uvIndex,
                        soil = SoilData(entity.soilMoisture, entity.soilTemperature)
                    ))
                }
        }

        // 2. Fetch fresh data from API and update database
        try {
            val response = apiService.getCurrentWeather(lat, lon)
            
            // Get current hourly indices (simplistic approach: just take first if not careful)
            // Ideally match with response.currentWeather.time
            val currentIdx = 0 
            
            val mappedResponse = WeatherResponse(
                main = MainData(
                    temp = response.currentWeather.temp,
                    feels_like = response.currentWeather.temp,
                    humidity = response.hourly.humidity.getOrNull(currentIdx)?.toInt() ?: 0,
                    evapotranspiration = response.hourly.evapotranspiration.getOrNull(currentIdx) ?: 0.0
                ),
                weather = listOf(
                    WeatherData(
                        description = mapWeatherCode(response.currentWeather.conditionCode),
                        icon = "01d"
                    )
                ),
                name = "Your Farm",
                wind = WindData(response.currentWeather.windspeed),
                uvIndex = response.hourly.uvIndex.getOrNull(currentIdx) ?: 0.0,
                soil = SoilData(
                    moisture = response.hourly.soilMoisture.getOrNull(currentIdx) ?: 0.0,
                    temperature = response.hourly.soilTemperature.getOrNull(currentIdx) ?: 0.0
                ),
                forecast = response.daily.time.indices.map { i ->
                    ForecastData(
                        date = response.daily.time[i],
                        tempMax = response.daily.tempMax[i],
                        tempMin = response.daily.tempMin[i],
                        description = mapWeatherCode(response.daily.weatherCode[i]),
                        icon = "01d"
                    )
                },
                advisory = generateAdvisory(
                    response.currentWeather.temp,
                    response.hourly.humidity.getOrNull(currentIdx) ?: 0.0
                )
            )

            // 3. Update cache
            weatherDao.cacheWeather(WeatherEntity(
                id = 1,
                lat = lat,
                lon = lon,
                cityName = mappedResponse.name,
                temperature = mappedResponse.main.temp,
                humidity = mappedResponse.main.humidity,
                description = mappedResponse.weather.first().description,
                icon = mappedResponse.weather.first().icon,
                windSpeed = mappedResponse.wind.speed,
                uvIndex = mappedResponse.uvIndex,
                soilMoisture = mappedResponse.soil?.moisture ?: 0.0,
                soilTemperature = mappedResponse.soil?.temperature ?: 0.0,
                evapotranspiration = mappedResponse.main.evapotranspiration,
                timestamp = System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        awaitClose { dbJob.cancel() }
    }.flowOn(Dispatchers.IO)

    private fun generateAdvisory(temp: Double, humidity: Double): SmartAdvisory {
        return when {
            humidity > 70 && temp > 25 -> SmartAdvisory(
                "Fungal Risk",
                "High humidity + warm = fungal risk. Apply fungicide today.",
                "DANGER"
            )
            temp < 4 -> SmartAdvisory(
                "Frost Warning",
                "Temperatures near freezing. Protect sensitive crops.",
                "WARNING"
            )
            else -> SmartAdvisory(
                "Optimal Conditions",
                "Conditions are good for general farm activities.",
                "SUCCESS"
            )
        }
    }

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
