package com.shoping.agrismartapp

import android.content.Context
import android.location.Geocoder
import java.util.Locale

data class RegionalInfo(
    val regionName: String,
    val soilType: String,
    val recommendedCrops: List<String>
)

object RegionalData {
    // A simplified mapping of regions to soil types and crops
    // In a production app, this would come from a Soil Map API
    fun getInfoByLocation(latitude: Double, longitude: Double): RegionalInfo {
        return when {
            // Northern Plains (Bihar, UP, Punjab, Haryana, Delhi)
            latitude in 24.0..32.0 && longitude in 74.0..88.0 -> {
                RegionalInfo(
                    "Indo-Gangetic Plains", 
                    "Alluvial & Loamy", 
                    listOf("Wheat", "Rice", "Maize", "Potato", "Tomato", "Mango", "Sugarcane", "Guava")
                )
            }
            // Central India & Deccan (Maharashtra, MP, parts of Karnataka)
            latitude in 15.0..24.0 && longitude in 73.0..82.0 -> {
                RegionalInfo(
                    "Central Plateau", 
                    "Black Soil (Regur)", 
                    listOf("Cotton", "Sugarcane", "Onion", "Grapes", "Brinjal", "Papaya", "Banana")
                )
            }
            // Coastal & Southern India (Kerala, Tamil Nadu, Andhra, Karnataka)
            latitude in 8.0..18.0 && longitude in 74.0..85.0 -> {
                RegionalInfo(
                    "Coastal/Southern Region", 
                    "Red & Laterite Soil", 
                    listOf("Rice", "Banana", "Tea", "Papaya", "Guava", "Tomato", "Brinjal")
                )
            }
            // Default / General (Hilly or Other regions)
            else -> {
                RegionalInfo(
                    "General Indian Region", 
                    "Loamy Soil", 
                    listOf("Wheat", "Tomato", "Onion", "Maize", "Potato", "Spinach", "Apple")
                )
            }
        }
    }

    fun getCityName(context: Context, lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val address = addresses?.get(0)
            val city = address?.locality ?: address?.subAdminArea ?: address?.adminArea
            city ?: "Indian Location"
        } catch (e: Exception) {
            "Indian Location"
        }
    }
}
