package com.shoping.agrismart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shoping.agrismart.data.local.entity.FAQEntity

@Dao
interface FAQDao {
    @Query("SELECT * FROM faqs WHERE question LIKE '%' || :query || '%'")
    suspend fun searchFaqs(query: String): List<FAQEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaqs(faqs: List<FAQEntity>)
}
