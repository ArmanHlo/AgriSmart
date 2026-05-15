package com.shoping.agrismartapp.data.local.dao

import androidx.room.*
import com.shoping.agrismartapp.data.local.entity.CropEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDao {
    @Query("SELECT * FROM crops")
    fun getAllCrops(): Flow<List<CropEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<CropEntity>)

    @Query("DELETE FROM crops")
    suspend fun deleteAllCrops()
}
