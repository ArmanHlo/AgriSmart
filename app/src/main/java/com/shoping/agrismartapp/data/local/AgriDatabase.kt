package com.shoping.agrismartapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shoping.agrismartapp.data.local.dao.CropDao
import com.shoping.agrismartapp.data.local.dao.FAQDao
import com.shoping.agrismartapp.data.local.dao.FarmActivityDao
import com.shoping.agrismartapp.data.local.dao.NoteDao
import com.shoping.agrismartapp.data.local.dao.WeatherDao
import com.shoping.agrismartapp.data.local.entity.CropEntity
import com.shoping.agrismartapp.data.local.entity.FAQEntity
import com.shoping.agrismartapp.data.local.entity.FarmActivityEntity
import com.shoping.agrismartapp.data.local.entity.NoteEntity
import com.shoping.agrismartapp.data.local.entity.WeatherEntity

@Database(
    entities = [
        CropEntity::class,
        FarmActivityEntity::class,
        WeatherEntity::class,
        FAQEntity::class,
        NoteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AgriDatabase : RoomDatabase() {
    abstract val cropDao: CropDao
    abstract val farmActivityDao: FarmActivityDao
    abstract val weatherDao: WeatherDao
    abstract val faqDao: FAQDao
    abstract val noteDao: NoteDao

    companion object {
        val CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seeding initial FAQs for offline support
                db.execSQL("INSERT INTO faqs (question, answer, category) VALUES ('How to control wheat rust?', 'Use fungicides like Tebuconazole or Propiconazole at recommended doses.', 'Pest Control')")
                db.execSQL("INSERT INTO faqs (question, answer, category) VALUES ('Best time for sowing rice?', 'Ideally during the onset of the monsoon (June-July) for Kharif season.', 'Crops')")
                db.execSQL("INSERT INTO faqs (question, answer, category) VALUES ('How to improve soil health?', 'Apply organic manure, practice crop rotation, and avoid excessive chemical fertilizers.', 'Soil')")
                db.execSQL("INSERT INTO faqs (question, answer, category) VALUES ('What is PM-Kisan scheme?', 'A government scheme providing ₹6,000 yearly to small and marginal farmers.', 'Govt Schemes')")
                db.execSQL("INSERT INTO faqs (question, answer, category) VALUES ('Drip irrigation benefits?', 'Saves water, reduces weed growth, and ensures targeted nutrient delivery.', 'Irrigation')")
            }
        }
    }
}
