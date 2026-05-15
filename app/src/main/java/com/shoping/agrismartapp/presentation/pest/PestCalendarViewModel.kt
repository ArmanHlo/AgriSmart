package com.shoping.agrismartapp.presentation.pest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.data.UserPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PestCalendarViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {

    private val _selectedCrop = MutableStateFlow("Wheat")
    val selectedCrop: StateFlow<String> = _selectedCrop.asStateFlow()

    private val _pests = MutableStateFlow<List<PestInfo>>(emptyList())
    val pests: StateFlow<List<PestInfo>> = _pests.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferenceManager.userPreferences.collect { prefs ->
                _selectedCrop.value = prefs.selectedCrop
                updatePestData(prefs.selectedCrop)
            }
        }
    }

    private fun updatePestData(crop: String) {
        val allPests = listOf(
            PestInfo("Aphids", "Jan-Mar", "High", "Neem Oil spray", "Wheat"),
            PestInfo("Yellow Rust", "Dec-Feb", "Critical", "Propiconazole spray", "Wheat"),
            PestInfo("Stem Borer", "Jun-Aug", "Very High", "Pheromone traps", "Rice (Paddy)"),
            PestInfo("Blast", "Aug-Oct", "High", "Tricyclazole", "Rice (Paddy)"),
            PestInfo("Bollworm", "Oct-Dec", "Critical", "Chlorpyrifos", "Cotton"),
            PestInfo("Whitefly", "Sep-Nov", "Medium", "Yellow sticky traps", "Cotton"),
            PestInfo("Early Blight", "All Season", "Medium", "Mancozeb", "Tomato"),
            PestInfo("Leaf Curl", "All Season", "High", "Control Whiteflies", "Tomato")
        )
        
        _pests.value = allPests.filter { it.targetCrop.contains(crop, ignoreCase = true) || crop.contains(it.targetCrop, ignoreCase = true) }
            .ifEmpty { allPests.take(4) }
    }
}

data class PestInfo(
    val name: String, 
    val season: String, 
    val risk: String, 
    val prevention: String,
    val targetCrop: String
)
