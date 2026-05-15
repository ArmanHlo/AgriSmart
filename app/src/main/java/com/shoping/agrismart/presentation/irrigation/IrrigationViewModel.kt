package com.shoping.agrismart.presentation.irrigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.repository.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IrrigationViewModel @Inject constructor(
    private val cropRepository: CropRepository,
    private val userPreferenceManager: com.shoping.agrismart.data.UserPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(IrrigationState())
    val state: StateFlow<IrrigationState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                _state.update { it.copy(
                    selectedCrop = prefs.selectedCrop,
                    selectedSoil = prefs.selectedSoil,
                    farmArea = "1" // Default
                ) }
                calculateSchedule()
            }
        }
    }

    fun onCropSelected(crop: String) {
        _state.update { it.copy(selectedCrop = crop) }
        calculateSchedule()
    }

    fun onSoilSelected(soil: String) {
        _state.update { it.copy(selectedSoil = soil) }
        calculateSchedule()
    }

    fun onAreaChanged(area: String) {
        _state.update { it.copy(farmArea = area) }
        calculateSchedule()
    }

    private fun calculateSchedule() {
        if (_state.value.selectedCrop.isEmpty() || _state.value.selectedSoil.isEmpty()) return

        // Refined Irrigation Logic based on ETc = ETo x Kc
        // ETo (Reference Evapotranspiration) normally comes from weather API (Open-Meteo)
        // Here we use a representative value (e.g., 4.5 mm/day for Indian summer)
        val ETo = 4.5 
        
        val Kc = when (_state.value.selectedCrop) {
            "Rice (Paddy)" -> 1.15
            "Sugarcane" -> 1.25
            "Wheat" -> 0.85
            "Cotton" -> 0.90
            "Maize" -> 1.05
            else -> 1.0
        }

        val ETc = ETo * Kc // mm/day
        
        val soilMultiplier = when (_state.value.selectedSoil) {
            "Sandy" -> 1.2 // High drainage
            "Clay" -> 0.8  // High retention
            else -> 1.0
        }

        val area = _state.value.farmArea.toDoubleOrNull() ?: 1.0
        // 1 mm of water over 1 acre = approx 4047 liters
        val litersPerAcrePerDay = ETc * 4047 * soilMultiplier
        val totalLiters = litersPerAcrePerDay * area

        _state.update { 
            it.copy(
                recommendedLiters = totalLiters,
                frequency = when {
                    _state.value.selectedSoil == "Sandy" -> "Every 1-2 days (High drainage)"
                    _state.value.selectedSoil == "Clay" -> "Every 5-7 days (Good retention)"
                    else -> "Every 3-4 days"
                }
            )
        }
    }
}

data class IrrigationState(
    val selectedCrop: String = "",
    val selectedSoil: String = "",
    val farmArea: String = "1",
    val recommendedLiters: Double = 0.0,
    val frequency: String = ""
)
