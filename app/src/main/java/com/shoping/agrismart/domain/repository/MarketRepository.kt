package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.MarketPrice
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    fun getMarketPrices(
        state: String? = null,
        commodity: String? = null
    ): Flow<List<MarketPrice>>
}
