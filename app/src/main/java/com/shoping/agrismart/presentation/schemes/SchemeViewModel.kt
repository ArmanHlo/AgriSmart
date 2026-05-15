package com.shoping.agrismart.presentation.schemes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.model.GovernmentScheme
import com.shoping.agrismart.domain.repository.SchemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchemeViewModel @Inject constructor(
    private val repository: SchemeRepository,
    private val userPreferenceManager: com.shoping.agrismart.data.UserPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(SchemeState())
    val state: StateFlow<SchemeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                loadSchemes(prefs.selectedLocation, prefs.selectedCrop)
            }
        }
    }

    private fun loadSchemes(stateFilter: String, cropFilter: String) {
        _state.update { it.copy(isLoading = true) }
        repository.getSchemes()
            .onEach { allSchemes ->
                val relevantSchemes = allSchemes.filter { scheme ->
                    scheme.state == "Central" || 
                    scheme.state.contains(stateFilter, ignoreCase = true) ||
                    scheme.benefit.contains(cropFilter, ignoreCase = true)
                }
                _state.update { it.copy(isLoading = false, schemes = relevantSchemes) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }
}

data class SchemeState(
    val isLoading: Boolean = false,
    val schemes: List<GovernmentScheme> = emptyList(),
    val error: String? = null
)
