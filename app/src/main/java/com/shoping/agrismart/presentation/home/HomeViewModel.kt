package com.shoping.agrismart.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.repository.AuthRepository
import com.shoping.agrismart.domain.repository.CropRepository
import com.shoping.agrismart.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cropRepository: CropRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeUser()
        loadDashboardData(28.6139, 77.2090) // Default Delhi coordinates
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
