package com.shoping.agrismart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "farm_activities")
data class FarmActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // Sowing, Irrigation, Fertilizer, Pesticide, Harvest, etc.
    val cropName: String,
    val date: Long, // Timestamp
    val quantity: Double? = null,
    val unit: String? = null,
    val cost: Double? = null,
    val income: Double? = null,
    val notes: String? = null
)
