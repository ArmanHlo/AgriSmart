package com.shoping.agrismartapp.presentation.disease

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.domain.model.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiseaseScannerViewModel @Inject constructor(
    private val classifier: PlantDiseaseClassifier
) : ViewModel() {

    private val _state = MutableStateFlow(DiseaseScannerState())
    val state: StateFlow<DiseaseScannerState> = _state.asStateFlow()

    fun onImageCaptured(bitmap: Bitmap, useGemini: Boolean = false) {
        _state.update { it.copy(isLoading = true, capturedImage = bitmap) }
        viewModelScope.launch {
            val result = classifier.classify(bitmap, useGemini)
            _state.update { it.copy(isLoading = false, scanResult = result) }
        }
    }

    fun toggleGemini(enabled: Boolean) {
        _state.update { it.copy(useGemini = enabled) }
    }

    fun resetScanner() {
        _state.update { DiseaseScannerState(useGemini = _state.value.useGemini) }
    }
}

data class DiseaseScannerState(
    val isLoading: Boolean = false,
    val capturedImage: Bitmap? = null,
    val scanResult: ScanResult? = null,
    val error: String? = null,
    val useGemini: Boolean = false
)
