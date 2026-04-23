package com.shoping.agrismart.di

import com.google.firebase.firestore.FirebaseFirestore
import com.shoping.agrismart.data.repository.CommunityRepositoryImpl
import com.shoping.agrismart.data.repository.VideoRepositoryImpl
import com.shoping.agrismart.domain.repository.CommunityRepository
import com.shoping.agrismart.domain.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommunityModule {

    @Provides
    @Singleton
    fun provideCommunityRepository(firestore: FirebaseFirestore): CommunityRepository {
        return CommunityRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideVideoRepository(): VideoRepository {
        return VideoRepositoryImpl()
    }
}
