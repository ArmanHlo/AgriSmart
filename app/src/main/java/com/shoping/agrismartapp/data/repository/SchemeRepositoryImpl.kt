package com.shoping.agrismartapp.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shoping.agrismartapp.domain.model.GovernmentScheme
import com.shoping.agrismartapp.domain.repository.SchemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SchemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : SchemeRepository {

    override fun getSchemes(): Flow<List<GovernmentScheme>> = flow {
        val jsonString = context.assets.open("government_schemes.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<GovernmentScheme>>() {}.type
        val schemes: List<GovernmentScheme> = gson.fromJson(jsonString, listType)
        emit(schemes)
    }.flowOn(Dispatchers.IO)
}
