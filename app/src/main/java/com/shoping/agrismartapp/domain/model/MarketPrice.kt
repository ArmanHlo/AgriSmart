package com.shoping.agrismartapp.domain.model

data class MarketPrice(
    val state: String,
    val district: String,
    val market: String,
    val commodity: String,
    val variety: String,
    val arrivalDate: String,
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val unit: String = "Quintal"
)
