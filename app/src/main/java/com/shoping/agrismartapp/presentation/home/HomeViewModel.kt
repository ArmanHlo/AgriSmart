package com.shoping.agrismartapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.domain.repository.AuthRepository
import com.shoping.agrismartapp.domain.repository.CropRepository
import com.shoping.agrismartapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cropRepository: CropRepository,
    private val authRepository: AuthRepository,
    private val userPreferenceManager: com.shoping.agrismartapp.data.UserPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeUser()
        observePreferencesAndLoadData()
    }

    private fun observePreferencesAndLoadData() {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                // Basic mapping of states to coordinates for demonstration
                val (lat, lon) = when (prefs.selectedLocation) {
                    "Punjab" -> 30.9010 to 75.8573 // Ludhiana
                    "Maharashtra" -> 19.0760 to 72.8777 // Mumbai
                    "Karnataka" -> 12.9716 to 77.5946 // Bangalore
                    else -> 28.6139 to 77.2090 // Delhi
                }
                loadDashboardData(lat, lon)
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun loadDashboardData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            combine(
                weatherRepository.getCurrentWeather(lat, lon),
                cropRepository.getCrops()
            ) { weather, crops ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        weather = weather,
                        crops = crops
                    )
                }
            }.catch { e ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unknown error occurred"
                    )
                }
            }.collect()
        }
    }
}
