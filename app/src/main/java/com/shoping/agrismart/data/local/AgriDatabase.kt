package com.shoping.agrismart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shoping.agrismart.data.local.dao.CropDao
import com.shoping.agrismart.data.local.dao.FarmActivityDao
import com.shoping.agrismart.data.local.entity.CropEntity
import com.shoping.agrismart.data.local.entity.FarmActivityEntity
import com.shoping.agrismart.data.local.entity.WeatherEntity

@Database(
    entities = [
        CropEntity::class,
        FarmActivityEntity::class,
        WeatherEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AgriDatabase : RoomDatabase() {
    abstract val cropDao: CropDao
    abstract val farmActivityDao: FarmActivityDao
}
