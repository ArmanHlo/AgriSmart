package com.shoping.agrismartapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.shoping.agrismartapp.data.repository.CommunityRepositoryImpl
import com.shoping.agrismartapp.data.repository.VideoRepositoryImpl
import com.shoping.agrismartapp.domain.repository.CommunityRepository
import com.shoping.agrismartapp.domain.repository.VideoRepository
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
