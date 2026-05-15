package com.shoping.agrismartapp.domain.model

import java.util.Date

data class ChatMessage(
    val id: String,
    val content: String,
    val role: MessageRole,
    val timestamp: Date = Date()
)

enum class MessageRole {
    USER, BOT
}
