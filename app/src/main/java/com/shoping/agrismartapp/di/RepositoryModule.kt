package com.shoping.agrismartapp.di

import com.shoping.agrismartapp.data.repository.AuthRepositoryImpl
import com.shoping.agrismartapp.data.repository.CropRepositoryImpl
import com.shoping.agrismartapp.data.repository.SchemeRepositoryImpl
import com.shoping.agrismartapp.domain.repository.AuthRepository
import com.shoping.agrismartapp.domain.repository.CropRepository
import com.shoping.agrismartapp.domain.repository.SchemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSchemeRepository(
        schemeRepositoryImpl: SchemeRepositoryImpl
    ): SchemeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCropRepository(
        cropRepositoryImpl: CropRepositoryImpl
    ): CropRepository
}
