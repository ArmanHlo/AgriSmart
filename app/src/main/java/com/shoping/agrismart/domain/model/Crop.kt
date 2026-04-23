package com.shoping.agrismart.domain.model

data class Crop(
    val id: Int,
    val name: String,
    val season: String,
    val soilTypes: List<String>,
    val states: List<String>,
    val waterNeed: String,
    val pH_min: Double,
    val pH_max: Double,
    val daysToHarvest: Int,
    val yieldPerAcre: Int,
    val msp: Int,
    val description: String,
    val fertilizer: String,
    val diseases: List<String>,
    val imageUrl: String
)
