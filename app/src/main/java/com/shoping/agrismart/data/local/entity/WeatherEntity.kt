package com.shoping.agrismart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val description: String,
    val icon: String,
    val windSpeed: Double = 0.0,
    val uvIndex: Double = 0.0,
    val soilMoisture: Double = 0.0,
    val soilTemperature: Double = 0.0,
    val evapotranspiration: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
