package com.shoping.agrismart.presentation.crop

import com.shoping.agrismart.domain.model.Crop

data class CropRecommendationState(
    val currentStep: Int = 1,
    val selectedLocation: String = "",
    val selectedSoilType: String = "",
    val selectedSeason: String = "",
    val waterSource: String = "",
    val budget: Float = 50000f,
    val recommendations: List<Crop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
