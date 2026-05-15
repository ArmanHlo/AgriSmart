package com.shoping.agrismartapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shoping.agrismartapp.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LOCATION_KEY = stringPreferencesKey("location")
    private val DISTRICT_KEY = stringPreferencesKey("district")
    private val SOIL_KEY = stringPreferencesKey("soil")
    private val CROP_KEY = stringPreferencesKey("crop")
    private val SEASON_KEY = stringPreferencesKey("season")

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            selectedLocation = prefs[LOCATION_KEY] ?: "Punjab",
            selectedDistrict = prefs[DISTRICT_KEY] ?: "Amritsar",
            selectedSoil = prefs[SOIL_KEY] ?: "Loamy",
            selectedCrop = prefs[CROP_KEY] ?: "Wheat",
            selectedSeason = prefs[SEASON_KEY] ?: "Rabi"
        )
    }

    suspend fun updateLocation(location: String, district: String) {
        context.dataStore.edit { prefs ->
            prefs[LOCATION_KEY] = location
            prefs[DISTRICT_KEY] = district
        }
    }

    suspend fun updateCrop(crop: String) {
        context.dataStore.edit { prefs ->
            prefs[CROP_KEY] = crop
        }
    }

    suspend fun updateSoilAndSeason(soil: String, season: String) {
        context.dataStore.edit { prefs ->
            prefs[SOIL_KEY] = soil
            prefs[SEASON_KEY] = season
        }
    }
}
