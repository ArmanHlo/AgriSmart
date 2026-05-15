package com.shoping.agrismartapp.di

import com.google.ai.client.generativeai.GenerativeModel
import com.shoping.agrismartapp.BuildConfig
import com.shoping.agrismartapp.data.UserPreferenceManager
import com.shoping.agrismartapp.data.local.dao.FAQDao
import com.shoping.agrismartapp.data.local.dao.NoteDao
import com.shoping.agrismartapp.data.repository.ChatRepositoryImpl
import com.shoping.agrismartapp.domain.repository.AuthRepository
import com.shoping.agrismartapp.domain.repository.ChatRepository
import com.shoping.agrismartapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        generativeModel: GenerativeModel,
        authRepository: AuthRepository,
        weatherRepository: WeatherRepository,
        userPreferenceManager: UserPreferenceManager,
        faqDao: FAQDao,
        noteDao: NoteDao
    ): ChatRepository {
        return ChatRepositoryImpl(
            generativeModel,
            authRepository,
            weatherRepository,
            userPreferenceManager,
            faqDao,
            noteDao
        )
    }
}
