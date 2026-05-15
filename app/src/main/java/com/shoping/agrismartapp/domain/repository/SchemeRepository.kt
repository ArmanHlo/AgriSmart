package com.shoping.agrismartapp.domain.repository

import com.shoping.agrismartapp.domain.model.GovernmentScheme
import kotlinx.coroutines.flow.Flow

interface SchemeRepository {
    fun getSchemes(): Flow<List<GovernmentScheme>>
}
