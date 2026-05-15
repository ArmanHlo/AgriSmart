package com.shoping.agrismart.presentation.chat

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismart.domain.model.ChatMessage
import com.shoping.agrismart.domain.model.MessageRole
import com.shoping.agrismart.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("prompt")?.let { prompt ->
            onSendMessage(prompt)
        }
    }

    fun onSendMessage(content: String, image: Bitmap? = null) {
        if (content.isBlank() && image == null) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            role = MessageRole.USER
        )

        // Keep a reference to the history BEFORE adding the new message
        val previousHistory = _state.value.messages

        _state.update { 
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            // Pass previousHistory and image
            repository.sendMessage(content, previousHistory, image)
                .catch { e ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to get response"
                        )
                    }
                }
                .collect { botMessage ->
                    _state.update { 
                        it.copy(
                            messages = it.messages + botMessage,
                            isLoading = false
                        )
                    }
                }
        }
    }
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
