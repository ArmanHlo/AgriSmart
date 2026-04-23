package com.shoping.agrismart.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.model.Comment
import com.shoping.agrismart.domain.model.Post
import com.shoping.agrismart.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        repository.getPosts()
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { posts ->
                _state.update { it.copy(isLoading = false, posts = posts) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onPostContentChange(content: String) {
        _state.update { it.copy(postContent = content) }
    }

    fun onTopicChange(topic: String) {
        _state.update { it.copy(selectedTopic = topic) }
    }

    fun createPost() {
        val content = _state.value.postContent
        val topic = _state.value.selectedTopic
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                repository.createPost(content, "Farmer", topic)
                _state.update { it.copy(postContent = "", isPostSheetVisible = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.likePost(postId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun togglePostSheet(visible: Boolean) {
        _state.update { it.copy(isPostSheetVisible = visible) }
    }

    fun onCommentChange(comment: String) {
        _state.update { it.copy(commentText = comment) }
    }

    fun selectPostForComments(postId: String?) {
        _state.update { it.copy(selectedPostId = postId, comments = emptyList()) }
        if (postId != null) {
            loadComments(postId)
        }
    }

    private fun loadComments(postId: String) {
        repository.getComments(postId)
            .onEach { comments ->
                _state.update { it.copy(comments = comments) }
            }
            .launchIn(viewModelScope)
    }

    fun submitComment() {
        val postId = _state.value.selectedPostId ?: return
        val content = _state.value.commentText
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                repository.addComment(postId, content, "Farmer")
                _state.update { it.copy(commentText = "") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}

data class CommunityState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val postContent: String = "",
    val selectedTopic: String = "General",
    val isPostSheetVisible: Boolean = false,
    val selectedPostId: String? = null,
    val commentText: String = "",
    val comments: List<Comment> = emptyList()
)
