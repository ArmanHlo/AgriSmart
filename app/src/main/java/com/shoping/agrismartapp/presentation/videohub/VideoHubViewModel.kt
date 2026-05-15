package com.shoping.agrismartapp.presentation.videohub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.domain.model.Video
import com.shoping.agrismartapp.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideoHubViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VideoHubState())
    val state: StateFlow<VideoHubState> = _state.asStateFlow()

    init {
        loadVideos()
    }

    private fun loadVideos() {
        _state.update { it.copy(isLoading = true) }
        repository.getVideos()
            .onEach { videos ->
                _state.update { it.copy(isLoading = false, videos = videos) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onCategorySelected(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        // In a real app, we would fetch filtered videos from repository
    }
}

data class VideoHubState(
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All"
)
