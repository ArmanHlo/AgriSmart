package com.shoping.agrismart.di

import com.google.ai.client.generativeai.GenerativeModel
import com.shoping.agrismart.BuildConfig
import com.shoping.agrismart.data.local.dao.FAQDao
import com.shoping.agrismart.data.local.dao.NoteDao
import com.shoping.agrismart.data.repository.ChatRepositoryImpl
import com.shoping.agrismart.domain.repository.AuthRepository
import com.shoping.agrismart.domain.repository.ChatRepository
import com.shoping.agrismart.domain.repository.WeatherRepository
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
        faqDao: FAQDao,
        noteDao: NoteDao
    ): ChatRepository {
        return ChatRepositoryImpl(generativeModel, authRepository, weatherRepository, faqDao, noteDao)
    }
}
