package com.shoping.agrismart.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.model.MarketPrice
import com.shoping.agrismart.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketPriceViewModel @Inject constructor(
    private val repository: MarketRepository,
    private val userPreferenceManager: com.shoping.agrismart.data.UserPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(MarketPriceState())
    val state: StateFlow<MarketPriceState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                fetchPrices(stateFilter = prefs.selectedLocation)
            }
        }
    }

    fun fetchPrices(stateFilter: String? = null, commodityFilter: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getMarketPrices(stateFilter, commodityFilter)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { prices ->
                    _state.update { it.copy(isLoading = false, prices = prices) }
                }
        }
    }
}

data class MarketPriceState(
    val isLoading: Boolean = false,
    val prices: List<MarketPrice> = emptyList(),
    val error: String? = null
)
