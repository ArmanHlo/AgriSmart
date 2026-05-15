package com.shoping.agrismartapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface SoilApiService {
    @GET("forecast")
    suspend fun getSoilData(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "soil_temperature_0cm,soil_moisture_0_to_1cm",
        @Query("forecast_days") days: Int = 1
    ): SoilResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"
    }
}

data class SoilResponse(
    @SerializedName("hourly") val hourly: HourlySoilData
)

data class HourlySoilData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("soil_temperature_0cm") val temperature: List<Double>,
    @SerializedName("soil_moisture_0_to_1cm") val moisture: List<Double>
)
