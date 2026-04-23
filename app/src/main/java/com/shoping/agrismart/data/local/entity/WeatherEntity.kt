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
    val timestamp: Long = System.currentTimeMillis()
)
