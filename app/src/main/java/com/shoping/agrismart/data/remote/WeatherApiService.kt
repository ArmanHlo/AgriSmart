package com.shoping.agrismart.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true,
        @Query("hourly") hourly: String = "relative_humidity_2m"
    ): OpenMeteoResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"
    }
}

data class OpenMeteoResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("hourly") val hourly: HourlyData
)

data class CurrentWeather(
    @SerializedName("temperature") val temp: Double,
    @SerializedName("windspeed") val windspeed: Double,
    @SerializedName("weathercode") val conditionCode: Int
)

data class HourlyData(
    @SerializedName("relative_humidity_2m") val humidity: List<Double>
)

data class WeatherResponse(
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherData>,
    @SerializedName("name") val name: String
)

data class MainData(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("humidity") val humidity: Int
)

data class WeatherData(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
