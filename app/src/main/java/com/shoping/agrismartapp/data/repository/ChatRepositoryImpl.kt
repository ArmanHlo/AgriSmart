package com.shoping.agrismartapp.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.shoping.agrismartapp.data.local.dao.FAQDao
import com.shoping.agrismartapp.data.local.dao.NoteDao
import com.shoping.agrismartapp.data.local.entity.NoteEntity
import com.shoping.agrismartapp.domain.model.ChatMessage
import com.shoping.agrismartapp.domain.model.MessageRole
import com.shoping.agrismartapp.domain.repository.AuthRepository
import com.shoping.agrismartapp.domain.repository.ChatRepository
import com.shoping.agrismartapp.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val authRepository: AuthRepository,
    private val weatherRepository: WeatherRepository,
    private val userPreferenceManager: com.shoping.agrismartapp.data.UserPreferenceManager,
    private val faqDao: FAQDao,
    private val noteDao: NoteDao
) : ChatRepository {

    override fun sendMessage(
        prompt: String, 
        history: List<ChatMessage>,
        image: Bitmap?
    ): Flow<ChatMessage> = flow {
        // 1. Check Offline FAQs first
        if (image == null && history.isEmpty()) {
            val localFaqs = faqDao.searchFaqs(prompt)
            if (localFaqs.isNotEmpty()) {
                emit(ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "[Offline FAQ] " + localFaqs.first().answer,
                    role = MessageRole.BOT
                ))
                return@flow
            }
        }

        // 2. Gather context for Gemini
        val user = authRepository.currentUser.first()
        val prefs = userPreferenceManager.userPreferences.first()
        val weather = weatherRepository.getCurrentWeather(28.6139, 77.2090).first()

        val systemContext = """
            You are KrishiBot, an expert farming assistant. 
            Farmer Context:
            - Name: ${user?.name ?: "Arjun Singh"}
            - Location: ${prefs.selectedLocation}, ${prefs.selectedDistrict}
            - Current Crop: ${prefs.selectedCrop}
            - Soil: ${prefs.selectedSoil}
            - Farm Size: ${user?.farmSize ?: "3 acres"}
            - Current Weather: ${weather.main.temp}°C, ${weather.weather.firstOrNull()?.description}
            
            Instructions:
            - Provide practical, expert advice on agriculture, pests, crops, and government schemes.
            - Default Language: English. Only respond in Hindi if the farmer explicitly asks in Hindi or asks you to speak in Hindi.
            - Tone: Extremely polite, helpful, and culturally respectful (Indian style). Use "Ji" or formal address where appropriate.
            - If an image is provided, analyze it for crop diseases or plant health issues.
            - Keep answers concise and actionable.
        """.trimIndent()

        // ... existing Gemini call logic ...
        val chatHistory = history.map { message ->
            content(role = if (message.role == MessageRole.USER) "user" else "model") {
                text(message.content)
            }
        }
        
        val chat = generativeModel.startChat(history = chatHistory)
        
        val response = if (image != null) {
            generativeModel.generateContent(content {
                text(systemContext + "\nUser Prompt: " + prompt)
                image(image)
            })
        } else {
            chat.sendMessage(content {
                text(systemContext + "\nUser Prompt: " + prompt)
            })
        }
        
        val botResponse = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = response.text ?: "I'm sorry, I couldn't process that.",
            role = MessageRole.BOT
        )

        // 3. Logic to "Save Answers" to My Notes
        if (prompt.contains("save", ignoreCase = true) && history.isNotEmpty()) {
            val lastBotMessage = history.lastOrNull { it.role == MessageRole.BOT }
            lastBotMessage?.let {
                noteDao.insertNote(NoteEntity(
                    title = "KrishiBot Tip: ${prompt.take(20)}...",
                    content = it.content
                ))
            }
        }
        
        emit(botResponse)
    }.flowOn(Dispatchers.IO)
}
