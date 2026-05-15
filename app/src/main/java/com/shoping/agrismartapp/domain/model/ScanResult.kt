package com.shoping.agrismartapp.domain.model

import java.util.Date

data class ScanResult(
    val id: String,
    val cropName: String,
    val diseaseName: String,
    val confidence: Float,
    val treatment: String,
    val timestamp: Date = Date(),
    val imageUri: String? = null
)
