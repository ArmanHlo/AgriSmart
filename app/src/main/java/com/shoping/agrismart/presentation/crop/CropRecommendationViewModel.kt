package com.shoping.agrismart.presentation.crop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.repository.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropRecommendationViewModel @Inject constructor(
    private val repository: CropRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CropRecommendationState())
    val state: StateFlow<CropRecommendationState> = _state.asStateFlow()

    fun onLocationSelected(location: String) {
        _state.update { it.copy(selectedLocation = location) }
    }

    fun onSoilTypeSelected(soilType: String) {
        _state.update { it.copy(selectedSoilType = soilType) }
    }

    fun onSeasonSelected(season: String) {
        _state.update { it.copy(selectedSeason = season) }
    }
    
    fun onWaterSourceSelected(source: String) {
        _state.update { it.copy(waterSource = source) }
    }
    
    fun onBudgetChanged(budget: Float) {
        _state.update { it.copy(budget = budget) }
    }

    fun nextStep() {
        if (_state.value.currentStep < 5) {
            _state.update { it.copy(currentStep = it.currentStep + 1) }
        } else {
            getRecommendations()
        }
    }

    fun previousStep() {
        if (_state.value.currentStep > 1) {
            _state.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    private fun getRecommendations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            repository.getRecommendedCrops(
                _state.value.selectedLocation,
                _state.value.selectedSoilType,
                _state.value.selectedSeason,
                _state.value.waterSource,
                _state.value.budget
            ).catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }.collect { crops ->
                _state.update { it.copy(recommendations = crops, currentStep = 6) }
                
                // After getting crops, fetch AI advice
                if (crops.isNotEmpty()) {
                    repository.getAiCropAdvice(
                        _state.value.selectedLocation,
                        _state.value.selectedSoilType,
                        _state.value.selectedSeason,
                        _state.value.waterSource,
                        _state.value.budget,
                        crops
                    ).collect { advice ->
                        _state.update { it.copy(isLoading = false, aiAdvice = advice) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
