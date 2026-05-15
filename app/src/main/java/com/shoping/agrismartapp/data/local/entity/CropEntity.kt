package com.shoping.agrismartapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val season: String,
    val soilTypes: String, // Stored as comma-separated string
    val states: String,    // Stored as comma-separated string
    val waterNeed: String,
    val pH_min: Double,
    val pH_max: Double,
    val daysToHarvest: Int,
    val yieldPerAcre: Int,
    val msp: Int,
    val description: String,
    val fertilizer: String,
    val diseases: String,  // Stored as comma-separated string
    val imageUrl: String
)
