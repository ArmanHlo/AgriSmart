package com.shoping.agrismartapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface MarketApiService {
    /**
     * Resource ID for "Current Daily Price of Various Commodities from Various Markets (Mandi)"
     * sourced from data.gov.in
     */
    @GET(MANDI_PRICE_RESOURCE_ID)
    suspend fun getMarketPrices(
        @Query("api-key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @Query("filters[state]") state: String? = null,
        @Query("filters[market]") market: String? = null,
        @Query("filters[commodity]") commodity: String? = null
    ): MarketResponse

    companion object {
        const val BASE_URL = "https://api.data.gov.in/resource/"
        const val MANDI_PRICE_RESOURCE_ID = "9ef84268-d588-465a-a308-a864a43d0070"
    }
}

data class MarketResponse(
    @SerializedName("records") val records: List<MarketRecord>
)

data class MarketRecord(
    @SerializedName("state") val state: String,
    @SerializedName("district") val district: String,
    @SerializedName("market") val market: String,
    @SerializedName("commodity") val commodity: String,
    @SerializedName("variety") val variety: String,
    @SerializedName("arrival_date") val arrivalDate: String,
    @SerializedName("min_price") val minPrice: String,
    @SerializedName("max_price") val maxPrice: String,
    @SerializedName("modal_price") val modalPrice: String
)
