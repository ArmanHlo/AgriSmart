package com.shoping.agrismart.presentation.schemes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.model.GovernmentScheme
import com.shoping.agrismart.domain.repository.SchemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SchemeViewModel @Inject constructor(
    private val repository: SchemeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SchemeState())
    val state: StateFlow<SchemeState> = _state.asStateFlow()

    init {
        loadSchemes()
    }

    private fun loadSchemes() {
        _state.update { it.copy(isLoading = true) }
        repository.getSchemes()
            .onEach { schemes ->
                _state.update { it.copy(isLoading = false, schemes = schemes) }
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
