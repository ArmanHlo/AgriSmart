package com.shoping.agrismartapp.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.data.local.dao.FarmActivityDao
import com.shoping.agrismartapp.data.local.entity.FarmActivityEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmJournalViewModel @Inject constructor(
    private val dao: FarmActivityDao
) : ViewModel() {

    val activities = dao.getAllActivities().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalExpenses = dao.getTotalExpenses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalIncome = dao.getTotalIncome().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun addActivity(
        type: String,
        cropName: String,
        date: Long,
        quantity: Double? = null,
        unit: String? = null,
        cost: Double? = null,
        income: Double? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            dao.insertActivity(
                FarmActivityEntity(
                    type = type,
                    cropName = cropName,
                    date = date,
                    quantity = quantity,
                    unit = unit,
                    cost = cost,
                    income = income,
                    notes = notes
                )
            )
        }
    }

    fun deleteActivity(activity: FarmActivityEntity) {
        viewModelScope.launch {
            dao.deleteActivity(activity)
        }
    }
}
