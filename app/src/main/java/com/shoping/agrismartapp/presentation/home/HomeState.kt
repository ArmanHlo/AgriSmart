package com.shoping.agrismartapp.presentation.home

import com.shoping.agrismartapp.data.remote.WeatherResponse
import com.shoping.agrismartapp.domain.model.Crop
import com.shoping.agrismartapp.domain.model.User

data class HomeState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val crops: List<Crop> = emptyList(),
    val user: User? = null,
    val error: String? = null
)
