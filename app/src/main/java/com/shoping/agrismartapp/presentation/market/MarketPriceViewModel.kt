package com.shoping.agrismartapp.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.domain.model.MarketPrice
import com.shoping.agrismartapp.domain.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketPriceViewModel @Inject constructor(
    private val repository: MarketRepository,
    private val userPreferenceManager: com.shoping.agrismartapp.data.UserPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(MarketPriceState())
    val state: StateFlow<MarketPriceState> = _state.asStateFlow()

    private var currentLocation: String? = null

    init {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                currentLocation = prefs.selectedLocation
                if (_state.value.searchQuery.isBlank()) {
                    fetchPrices(stateFilter = currentLocation)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.length >= 3) {
            fetchPrices(commodityFilter = query)
        } else if (query.isEmpty()) {
            fetchPrices(stateFilter = currentLocation)
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
    val error: String? = null,
    val searchQuery: String = ""
)
