package com.shoping.agrismartapp.domain.repository

import android.graphics.Bitmap
import com.shoping.agrismartapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling AI Chatbot interactions.
 */
interface ChatRepository {
    /**
     * Sends a message to the AI and returns a Flow of the response message.
     */
    fun sendMessage(
        prompt: String, 
        history: List<ChatMessage>,
        image: Bitmap? = null
    ): Flow<ChatMessage>
}
