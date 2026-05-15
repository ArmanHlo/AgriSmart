package com.shoping.agrismartapp.data.local.dao

import androidx.room.*
import com.shoping.agrismartapp.data.local.entity.FarmActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmActivityDao {
    @Query("SELECT * FROM farm_activities ORDER BY date DESC")
    fun getAllActivities(): Flow<List<FarmActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: FarmActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: FarmActivityEntity)

    @Query("SELECT SUM(cost) FROM farm_activities")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT SUM(income) FROM farm_activities")
    fun getTotalIncome(): Flow<Double?>
}
