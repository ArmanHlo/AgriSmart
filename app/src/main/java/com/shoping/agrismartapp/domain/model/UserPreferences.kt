package com.shoping.agrismartapp.domain.model

data class UserPreferences(
    val selectedLocation: String = "Punjab",
    val selectedDistrict: String = "Amritsar",
    val selectedSoil: String = "Loamy",
    val selectedCrop: String = "Wheat",
    val selectedSeason: String = "Rabi"
)
