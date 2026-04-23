package com.shoping.agrismart.presentation.home

import com.shoping.agrismart.data.remote.WeatherResponse
import com.shoping.agrismart.domain.model.Crop
import com.shoping.agrismart.domain.model.User

data class HomeState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val crops: List<Crop> = emptyList(),
    val user: User? = null,
    val error: String? = null
)
