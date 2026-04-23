package com.shoping.agrismart.di

import com.google.ai.client.generativeai.GenerativeModel
import com.shoping.agrismart.BuildConfig
import com.shoping.agrismart.data.repository.ChatRepositoryImpl
import com.shoping.agrismart.domain.repository.ChatRepository
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
    fun provideChatRepository(generativeModel: GenerativeModel): ChatRepository {
        return ChatRepositoryImpl(generativeModel)
    }
}
