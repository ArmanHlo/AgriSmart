package com.shoping.agrismartapp.presentation.weather

import android.Manifest
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shoping.agrismartapp.presentation.home.HomeViewModel
import com.shoping.agrismartapp.presentation.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val weather = state.weather
    val scrollState = rememberScrollState()

    var selectedHour by remember { mutableIntStateOf(1) } // Default to index 1 (10 AM in the hardcoded list)

    // Location Permission State
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        if (locationPermissionState.status.isGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                WeatherHeader(onBack)
                
                Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                    WeatherHeroSection(
                        temp = "${weather?.main?.temp?.toInt() ?: 28}°C",
                        condition = weather?.weather?.firstOrNull()?.description ?: "Partly Cloudy",
                        feelsLike = "${weather?.main?.feels_like?.toInt() ?: 31}°C"
                    )
                    
                    Spacer(Modifier.height(Spacing.xl))
                    SectionHeader(title = "Hourly Forecast", subtitle = "Next 24 hours")
                    HourlyForecastStrip(selectedHour) { selectedHour = it }
                    
                    val displayHumidity = if (selectedHour == 1) "${weather?.main?.humidity ?: 71}%" else "${(60..85).random()}%"
                    val displayWind = if (selectedHour == 1) "12 km/h" else "${(8..18).random()} km/h"
                    val displayUV = if (selectedHour == 1) "6 (High)" else "${(3..9).random()} (Mod)"
                    val displayVisibility = if (selectedHour == 1) "10 km" else "${(8..12).random()} km"

                    Spacer(Modifier.height(Spacing.xl))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        WeatherMetricCard("Humidity", displayHumidity, Icons.Default.WaterDrop, BrandSky, Modifier.weight(1f))
                        WeatherMetricCard("Wind", displayWind, Icons.Default.Air, SuccessGreen, Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(Spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        WeatherMetricCard("UV Index", displayUV, Icons.Default.WbSunny, BrandAmber, Modifier.weight(1f))
                        WeatherMetricCard("Visibility", displayVisibility, Icons.Default.Visibility, InfoBlue, Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(Spacing.xl))
                    SectionHeader(title = "7-Day Forecast", actionText = "Details")
                    SevenDayForecastList(weather?.forecast ?: emptyList())
                    
                    Spacer(Modifier.height(Spacing.xl))
                    FarmingAdvisoryWeather(weather?.weather?.firstOrNull()?.description ?: "")
                    
                    Spacer(Modifier.height(Spacing.huge))
                }
            }
        } else {
            // Permission Denied View
            Column(
                modifier = Modifier.fillMaxSize().padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.LocationOff, null, modifier = Modifier.size(80.dp), tint = DarkTextSub)
                Spacer(Modifier.height(Spacing.m))
                Text("Location Access Required", style = TypographyTokens.HeadingM)
                Text(
                    "We need your location to show accurate weather for your farm.",
                    style = TypographyTokens.BodyM,
                    color = DarkTextSub,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Spacing.xl))
                GlowButton(text = "Grant Permission", onClick = { locationPermissionState.launchPermissionRequest() })
                TextButton(onClick = onBack) { Text("Go Back", color = DarkTextSub) }
            }
        }
    }
}

@Composable
fun WeatherHeader(onBack: () -> Unit) {
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
        Column {
            Text("Ludhiana, Punjab", style = TypographyTokens.HeadingM)
            Text("Updated just now", style = TypographyTokens.Micro, color = DarkTextSub)
        }
    }
}

@Composable
fun WeatherHeroSection(temp: String, condition: String, feelsLike: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .glowShadow(color = BrandAmber.copy(0.2f), blurRadius = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.WbSunny, null, modifier = Modifier.size(100.dp), tint = BrandAmber)
        }
        
        Text(
            text = temp,
            style = TypographyTokens.DisplayXL.copy(fontSize = 72.sp),
            color = Color.White,
            fontFamily = JetBrainsMono
        )
        
        Text(
            text = condition.uppercase(),
            style = TypographyTokens.HeadingM,
            color = DarkText,
            letterSpacing = 2.sp
        )
        
        Spacer(Modifier.height(Spacing.s))
        StatusPill(text = "Feels like $feelsLike", type = StatusType.INFO)
    }
}

@Composable
fun HourlyForecastStrip(selectedHour: Int, onHourSelected: (Int) -> Unit) {
    val hours = (9..20).map { if (it > 12) "${it-12} PM" else "$it AM" }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        contentPadding = PaddingValues(end = Spacing.md)
    ) {
        itemsIndexed(hours) { index, hour ->
            val isSelected = index == selectedHour
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clip(ShapeM)
                    .background(if (isSelected) BrandGreen else DarkSurface2)
                    .border(1.dp, if (isSelected) BrandGreenGlow else DarkBorder, ShapeM)
                    .clickable { onHourSelected(index) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(hour, style = TypographyTokens.Micro, color = if (isSelected) Color.White else DarkTextSub)
                Spacer(Modifier.height(Spacing.s))
                Icon(Icons.Default.Cloud, null, modifier = Modifier.size(20.dp), tint = if (isSelected) Color.White else BrandSky)
                Spacer(Modifier.height(Spacing.s))
                Text(if (isSelected) "28°" else "26°", style = TypographyTokens.BodyM, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun WeatherMetricCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    KrishiCard(modifier = modifier, gradient = DarkSurface.toBrush()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(Spacing.s))
            Text(label, style = TypographyTokens.Micro, color = DarkTextSub)
            Text(value, style = TypographyTokens.HeadingS, color = Color.White)
        }
    }
}

@Composable
fun SevenDayForecastList(forecast: List<com.shoping.agrismartapp.data.remote.ForecastData>) {
    val displayForecast = if (forecast.isEmpty()) {
        // Fallback mock data with variation if API hasn't returned forecast yet
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        days.mapIndexed { index, day ->
            com.shoping.agrismartapp.data.remote.ForecastData(
                date = day,
                tempMax = (28..34).random().toDouble(),
                tempMin = (18..24).random().toDouble(),
                description = "Clear",
                icon = ""
            )
        }
    } else forecast

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
        displayForecast.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeM)
                    .background(DarkSurface2)
                    .padding(horizontal = Spacing.md, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.date.takeLast(3), 
                    modifier = Modifier.width(48.dp), 
                    style = TypographyTokens.BodyM,
                    color = Color.White // Fixed: Made clearly visible
                )
                
                Icon(
                    imageVector = if (day.tempMax > 30) Icons.Default.WbSunny else Icons.Default.WbCloudy, 
                    contentDescription = null, 
                    tint = if (day.tempMax > 30) BrandAmber else DarkTextSub, 
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(Modifier.width(Spacing.m))
                
                // Varied progress bar based on temperature
                Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(DarkBorder)) {
                    val progress = ((day.tempMax - 10) / 40).toFloat().coerceIn(0.1f, 1f)
                    Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(
                        if (day.tempMax > 30) BrandAmber else BrandSky
                    ))
                }
                
                Spacer(Modifier.width(Spacing.m))
                
                Text(
                    text = "${day.tempMax.toInt()}°", 
                    style = TypographyTokens.BodyM, 
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.width(Spacing.s))
                Text(
                    text = "${day.tempMin.toInt()}°", 
                    style = TypographyTokens.BodyM, 
                    color = DarkTextSub
                )
            }
        }
    }
}

@Composable
fun FarmingAdvisoryWeather(condition: String) {
    val advisory = when {
        condition.contains("rain", ignoreCase = true) -> "🌧 High rain risk. Secure your storage and avoid pesticide spray."
        else -> "✅ Perfect weather for sowing and fertilizer application. Soil moisture is optimal."
    }
    
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = Brush.linearGradient(listOf(BrandGreen, BrandGreenLight)),
        glowColor = BrandGreenGlow.copy(0.3f)
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.White)
                Spacer(Modifier.width(Spacing.m))
                Text("Farming Advisory", style = TypographyTokens.HeadingS, color = Color.White)
            }
            Spacer(Modifier.height(Spacing.md))
            Text(advisory, style = TypographyTokens.BodyM, color = Color.White.copy(0.9f))
        }
    }
}
