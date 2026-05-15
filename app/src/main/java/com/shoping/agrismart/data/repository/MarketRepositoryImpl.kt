package com.shoping.agrismart.data.repository

import com.shoping.agrismart.BuildConfig
import com.shoping.agrismart.data.remote.MarketApiService
import com.shoping.agrismart.domain.model.MarketPrice
import com.shoping.agrismart.domain.repository.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val apiService: MarketApiService
) : MarketRepository {

    override fun getMarketPrices(
        state: String?,
        commodity: String?
    ): Flow<List<MarketPrice>> = flow {
        try {
            val apiKey = BuildConfig.DATA_GOV_IN_API_KEY
            if (apiKey.isBlank()) throw Exception("API Key Missing")
            
            val response = apiService.getMarketPrices(
                apiKey = apiKey,
                state = state,
                commodity = commodity,
                limit = 50
            )
            
            val marketPrices = response.records.map { record ->
                MarketPrice(
                    state = record.state,
                    district = record.district,
                    market = record.market,
                    commodity = record.commodity,
                    variety = record.variety,
                    arrivalDate = record.arrivalDate,
                    minPrice = record.minPrice.toDoubleOrNull() ?: 0.0,
                    maxPrice = record.maxPrice.toDoubleOrNull() ?: 0.0,
                    modalPrice = record.modalPrice.toDoubleOrNull() ?: 0.0,
                    unit = "Quintal" // Default for data.gov.in mandi prices
                )
            }
            emit(marketPrices)
        } catch (e: Exception) {
            // FALLBACK TO MOCK DATA IF API FAILS
            emit(getMockMarketPrices())
        }
    }.flowOn(Dispatchers.IO)

    private fun getMockMarketPrices(): List<MarketPrice> {
        return listOf(
            MarketPrice("Maharashtra", "Nashik", "Nashik Mandi", "Onion", "Red", "12/05/2024", 1200.0, 1800.0, 1550.0, "Quintal"),
            MarketPrice("Punjab", "Amritsar", "Amritsar Mandi", "Wheat", "Kalyan", "12/05/2024", 2100.0, 2400.0, 2275.0, "Quintal"),
            MarketPrice("Uttar Pradesh", "Agra", "Agra Mandi", "Potato", "Desi", "12/05/2024", 800.0, 1200.0, 1050.0, "Quintal"),
            MarketPrice("Karnataka", "Bangalore", "Kolar Mandi", "Tomato", "Local", "12/05/2024", 1500.0, 2500.0, 2100.0, "Quintal"),
            MarketPrice("Madhya Pradesh", "Indore", "Indore Mandi", "Soyabean", "Yellow", "12/05/2024", 4200.0, 4800.0, 4550.0, "Quintal"),
            MarketPrice("Gujarat", "Rajkot", "Rajkot Mandi", "Cotton", "Shankar 6", "12/05/2024", 6500.0, 7500.0, 7100.0, "Quintal"),
            MarketPrice("Andhra Pradesh", "Guntur", "Guntur Mandi", "Chilli", "Teja", "12/05/2024", 18000.0, 22000.0, 20500.0, "Quintal")
        )
    }
}
