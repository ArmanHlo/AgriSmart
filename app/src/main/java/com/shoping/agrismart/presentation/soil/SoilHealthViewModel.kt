package com.shoping.agrismart.presentation.soil

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SoilHealthState(
    val ph: String = "",
    val nitrogen: String = "",
    val phosphorus: String = "",
    val potassium: String = "",
    val result: String? = null,
    val recommendations: List<String> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SoilHealthViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SoilHealthState())
    val state = _state.asStateFlow()

    fun onPhChange(value: String) {
        if (value.isEmpty() || value == "." || value.toDoubleOrNull() != null) {
            _state.update { it.copy(ph = value) }
        }
    }

    fun onNitrogenChange(value: String) {
        _state.update { it.copy(nitrogen = value) }
    }

    fun onPhosphorusChange(value: String) {
        _state.update { it.copy(phosphorus = value) }
    }

    fun onPotassiumChange(value: String) {
        _state.update { it.copy(potassium = value) }
    }

    fun analyzeSoil() {
        val ph = _state.value.ph.toDoubleOrNull() ?: 7.0
        val n = _state.value.nitrogen.toIntOrNull() ?: 0
        val p = _state.value.phosphorus.toIntOrNull() ?: 0
        val k = _state.value.potassium.toIntOrNull() ?: 0

        val recommendations = mutableListOf<String>()
        val resultText: String

        // pH Analysis
        when {
            ph < 6.0 -> {
                resultText = "Soil is Acidic"
                recommendations.add("Apply lime to increase pH level.")
            }
            ph > 7.5 -> {
                resultText = "Soil is Alkaline"
                recommendations.add("Apply sulfur or gypsum to lower pH level.")
            }
            else -> {
                resultText = "Soil pH is Healthy"
            }
        }

        // NPK Analysis (Simplified thresholds)
        if (n < 50) recommendations.add("Nitrogen levels are low. Use Urea or organic compost.")
        if (p < 20) recommendations.add("Phosphorus levels are low. Use Single Super Phosphate (SSP).")
        if (k < 40) recommendations.add("Potassium levels are low. Apply Muriate of Potash (MOP).")

        if (recommendations.isEmpty()) {
            recommendations.add("Your soil nutrients are well-balanced. Maintain with regular organic matter.")
        }

        _state.update { it.copy(
            result = resultText,
            recommendations = recommendations
        ) }
    }

    fun reset() {
        _state.value = SoilHealthState()
    }
}
