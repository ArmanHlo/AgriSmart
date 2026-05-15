package com.shoping.agrismartapp.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shoping.agrismartapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmingCalendarScreen(
    onBack: () -> Unit
) {
    val tasks = listOf(
        "Mon: Irrigation for Wheat",
        "Tue: Fertilizer application",
        "Wed: Pest monitoring",
        "Fri: Harvest window starts"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farming Calendar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                KrishiCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradient = DarkSurface2.toBrush()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = BrandGreenGlow)
                        Spacer(Modifier.width(12.dp))
                        Text(task, style = TypographyTokens.BodyL, color = Color.White)
                    }
                }
            }
        }
    }
}
