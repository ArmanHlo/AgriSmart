package com.shoping.agrismart.di

import android.content.Context
import androidx.room.Room
import com.shoping.agrismart.data.local.AgriDatabase
import com.shoping.agrismart.data.local.dao.CropDao
import com.shoping.agrismart.data.local.dao.FarmActivityDao
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
        ).build()
    }

    @Provides
    fun provideCropDao(database: AgriDatabase): CropDao {
        return database.cropDao
    }

    @Provides
    fun provideFarmActivityDao(database: AgriDatabase): FarmActivityDao {
        return database.farmActivityDao
    }
}
