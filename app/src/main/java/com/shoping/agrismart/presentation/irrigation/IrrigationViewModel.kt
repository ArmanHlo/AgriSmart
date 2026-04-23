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
    private val cropRepository: CropRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IrrigationState())
    val state: StateFlow<IrrigationState> = _state.asStateFlow()

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

        // Simple Irrigation Logic based on common agricultural ETc values
        val baseWaterPerAcre = when (_state.value.selectedCrop) {
            "Rice (Paddy)" -> 5000.0
            "Sugarcane" -> 4000.0
            "Wheat" -> 2000.0
            "Cotton" -> 2500.0
            else -> 1500.0
        }

        val soilMultiplier = when (_state.value.selectedSoil) {
            "Sandy" -> 1.5 // Dries faster
            "Clay" -> 0.7  // Holds water longer
            else -> 1.0
        }

        val area = _state.value.farmArea.toDoubleOrNull() ?: 1.0
        val totalLiters = baseWaterPerAcre * soilMultiplier * area

        _state.update { 
            it.copy(
                recommendedLiters = totalLiters,
                frequency = if (_state.value.selectedSoil == "Sandy") "Every 2 days" else "Every 4-5 days"
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
