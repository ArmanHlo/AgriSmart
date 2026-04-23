package com.shoping.agrismart.di

import com.shoping.agrismart.data.repository.AuthRepositoryImpl
import com.shoping.agrismart.data.repository.CropRepositoryImpl
import com.shoping.agrismart.data.repository.SchemeRepositoryImpl
import com.shoping.agrismart.domain.repository.AuthRepository
import com.shoping.agrismart.domain.repository.CropRepository
import com.shoping.agrismart.domain.repository.SchemeRepository
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
