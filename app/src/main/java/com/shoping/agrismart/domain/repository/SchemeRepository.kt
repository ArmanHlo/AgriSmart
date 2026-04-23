package com.shoping.agrismart.domain.repository

import com.shoping.agrismart.domain.model.GovernmentScheme
import kotlinx.coroutines.flow.Flow

interface SchemeRepository {
    fun getSchemes(): Flow<List<GovernmentScheme>>
}
