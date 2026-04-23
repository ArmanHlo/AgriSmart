package com.shoping.agrismart.di

import com.shoping.agrismart.data.remote.MarketApiService
import com.shoping.agrismart.data.remote.SoilApiService
import com.shoping.agrismart.data.remote.WeatherApiService
import com.shoping.agrismart.data.repository.MarketRepositoryImpl
import com.shoping.agrismart.data.repository.SoilRepositoryImpl
import com.shoping.agrismart.data.repository.WeatherRepositoryImpl
import com.shoping.agrismart.domain.repository.MarketRepository
import com.shoping.agrismart.domain.repository.SoilRepository
import com.shoping.agrismart.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(okHttpClient: OkHttpClient): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMarketApiService(okHttpClient: OkHttpClient): MarketApiService {
        return Retrofit.Builder()
            .baseUrl(MarketApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MarketApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSoilApiService(okHttpClient: OkHttpClient): SoilApiService {
        return Retrofit.Builder()
            .baseUrl(SoilApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(SoilApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideMarketRepository(apiService: MarketApiService): MarketRepository {
        return MarketRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideSoilRepository(apiService: SoilApiService): SoilRepository {
        return SoilRepositoryImpl(apiService)
    }
}
