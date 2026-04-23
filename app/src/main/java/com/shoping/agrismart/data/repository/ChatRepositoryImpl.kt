package com.shoping.agrismart.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.shoping.agrismart.domain.model.ChatMessage
import com.shoping.agrismart.domain.model.MessageRole
import com.shoping.agrismart.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : ChatRepository {

    override fun sendMessage(prompt: String, history: List<ChatMessage>): Flow<ChatMessage> = flow {
        val chatHistory = history.map { message ->
            content(role = if (message.role == MessageRole.USER) "user" else "model") {
                text(message.content)
            }
        }
        
        val chat = generativeModel.startChat(history = chatHistory)
        val response = chat.sendMessage(prompt)
        
        emit(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = response.text ?: "I'm sorry, I couldn't process that.",
                role = MessageRole.BOT
            )
        )
    }.flowOn(Dispatchers.IO)
}
