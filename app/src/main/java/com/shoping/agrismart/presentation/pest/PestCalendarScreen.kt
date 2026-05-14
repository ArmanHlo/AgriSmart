package com.shoping.agrismart.presentation.pest

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shoping.agrismart.R
import com.shoping.agrismart.presentation.theme.*

@Composable
fun PestCalendarScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentMonth = remember { java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) }
    
    val pestData = listOf(
        PestInfo("Aphids", "Jan-Mar", "High", "Neem Oil spray"),
        PestInfo("Stem Borer", "Jun-Aug", "Very High", "Pheromone traps"),
        PestInfo("Whitefly", "Sep-Nov", "Medium", "Yellow sticky traps"),
        PestInfo("Bollworm", "Oct-Dec", "Critical", "Chlorpyrifos spray")
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            PestHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                KrishiCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradient = GradientAmber,
                    glowColor = BrandAmber.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.xl),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BugReport, null, modifier = Modifier.size(32.dp), tint = Color.White)
                        }
                        Spacer(Modifier.width(Spacing.m))
                        Column {
                            Text(stringResource(R.string.pest_calendar), style = TypographyTokens.HeadingM, color = Color.White)
                            Text("Seasonal threat monitoring", style = TypographyTokens.BodyS, color = Color.White.copy(0.8f))
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.xl))
                
                SectionHeader(title = "Monthly Threats", subtitle = "Based on current season")

                pestData.forEach { pest ->
                    PestCard(pest)
                }

                Spacer(Modifier.height(Spacing.huge))
            }
        }
    }
}

@Composable
fun PestHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(Spacing.md)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        Spacer(Modifier.width(Spacing.m))
        Text(stringResource(R.string.pest_calendar), style = TypographyTokens.HeadingL)
    }
}

@Composable
fun PestCard(pest: PestInfo) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.m),
        gradient = DarkSurface.toBrush()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, null, tint = if(pest.risk == "Critical") DangerRed else WarningAmber)
            Spacer(Modifier.width(Spacing.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(pest.name, style = TypographyTokens.HeadingS)
                Text("Season: ${pest.season}", style = TypographyTokens.Micro, color = DarkTextSub)
            }
            StatusPill(text = pest.risk, type = if(pest.risk == "Critical") StatusType.ERROR else StatusType.WARNING)
        }
    }
}

data class PestInfo(val name: String, val season: String, val risk: String, val prevention: String)
