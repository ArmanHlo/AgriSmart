package com.shoping.agrismartapp.di

import android.content.Context
import androidx.room.Room
import com.shoping.agrismartapp.data.local.AgriDatabase
import com.shoping.agrismartapp.data.local.dao.CropDao
import com.shoping.agrismartapp.data.local.dao.FAQDao
import com.shoping.agrismartapp.data.local.dao.FarmActivityDao
import com.shoping.agrismartapp.data.local.dao.NoteDao
import com.shoping.agrismartapp.data.local.dao.WeatherDao
import com.shoping.agrismartapp.data.repository.NoteRepositoryImpl
import com.shoping.agrismartapp.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AgriDatabase {
        return Room.databaseBuilder(
            context,
            AgriDatabase::class.java,
            "agri_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(AgriDatabase.CALLBACK)
        .build()
    }

    @Provides
    fun provideCropDao(database: AgriDatabase): CropDao {
        return database.cropDao
    }

    @Provides
    fun provideFarmActivityDao(database: AgriDatabase): FarmActivityDao {
        return database.farmActivityDao
    }

    @Provides
    fun provideWeatherDao(database: AgriDatabase): WeatherDao {
        return database.weatherDao
    }

    @Provides
    fun provideFAQDao(database: AgriDatabase): FAQDao {
        return database.faqDao
    }

    @Provides
    fun provideNoteDao(database: AgriDatabase): NoteDao {
        return database.noteDao
    }

    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(noteDao)
    }
}
