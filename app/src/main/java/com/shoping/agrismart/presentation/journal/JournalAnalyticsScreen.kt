package com.shoping.agrismart.presentation.journal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalAnalyticsScreen(
    onBack: () -> Unit,
    viewModel: FarmJournalViewModel = hiltViewModel()
) {
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Profit/Loss Distribution", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AndroidView(
                    factory = { context ->
                        PieChart(context).apply {
                            description.isEnabled = false
                            setUsePercentValues(true)
                            setEntryLabelColor(android.graphics.Color.BLACK)
                        }
                    },
                    update = { pieChart ->
                        val entries = listOf(
                            PieEntry(totalExpenses?.toFloat() ?: 0f, "Expenses"),
                            PieEntry(totalIncome?.toFloat() ?: 0f, "Income")
                        )
                        val dataSet = PieDataSet(entries, "Farm Finances").apply {
                            colors = listOf(
                                android.graphics.Color.RED,
                                android.graphics.Color.GREEN
                            )
                            valueTextSize = 14f
                        }
                        pieChart.data = PieData(dataSet)
                        pieChart.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val profit = (totalIncome ?: 0.0) - (totalExpenses ?: 0.0)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (profit >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Net Profit/Loss", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "₹$profit",
                        style = MaterialTheme.typography.displaySmall,
                        color = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
