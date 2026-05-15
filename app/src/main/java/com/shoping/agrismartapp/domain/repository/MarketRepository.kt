package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.domain.model.MarketPrice
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    fun getMarketPrices(
        state: String? = null,
        commodity: String? = null
    ): Flow<List<MarketPrice>>
}
