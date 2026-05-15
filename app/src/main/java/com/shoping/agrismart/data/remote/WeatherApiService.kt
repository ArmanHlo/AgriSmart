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
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,precipitation_probability,soil_temperature_0_to_7cm,soil_moisture_0_to_7cm,et0_fao_evapotranspiration,uv_index",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"
    }
}

data class OpenMeteoResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("hourly") val hourly: HourlyData,
    @SerializedName("daily") val daily: DailyData
)

data class CurrentWeather(
    @SerializedName("temperature") val temp: Double,
    @SerializedName("windspeed") val windspeed: Double,
    @SerializedName("weathercode") val conditionCode: Int,
    @SerializedName("time") val time: String
)

data class HourlyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("relative_humidity_2m") val humidity: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerializedName("soil_temperature_0_to_7cm") val soilTemperature: List<Double>,
    @SerializedName("soil_moisture_0_to_7cm") val soilMoisture: List<Double>,
    @SerializedName("et0_fao_evapotranspiration") val evapotranspiration: List<Double>,
    @SerializedName("uv_index") val uvIndex: List<Double>
)

data class DailyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weathercode") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>
)

data class WeatherResponse(
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherData>,
    @SerializedName("name") val name: String,
    @SerializedName("wind") val wind: WindData = WindData(0.0),
    @SerializedName("uv_index") val uvIndex: Double = 0.0,
    @SerializedName("soil") val soil: SoilData? = null,
    @SerializedName("forecast") val forecast: List<ForecastData> = emptyList(),
    @SerializedName("advisory") val advisory: SmartAdvisory? = null
)

data class MainData(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("evapotranspiration") val evapotranspiration: Double = 0.0
)

data class WindData(
    @SerializedName("speed") val speed: Double
)

data class SoilData(
    @SerializedName("moisture") val moisture: Double,
    @SerializedName("temperature") val temperature: Double
)

data class ForecastData(
    @SerializedName("date") val date: String,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class SmartAdvisory(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("risk_level") val riskLevel: String // SUCCESS, WARNING, DANGER
)

data class WeatherData(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
