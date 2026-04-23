package com.shoping.agrismart.presentation.weather

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.shoping.agrismart.presentation.home.HomeViewModel
import com.shoping.agrismart.presentation.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val weather = state.weather
    val scrollState = rememberScrollState()

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
                    HourlyForecastStrip()
                    
                    Spacer(Modifier.height(Spacing.xl))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        WeatherMetricCard("Humidity", "${weather?.main?.humidity ?: 71}%", Icons.Default.WaterDrop, BrandSky, Modifier.weight(1f))
                        WeatherMetricCard("Wind", "12 km/h", Icons.Default.Air, SuccessGreen, Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(Spacing.md))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        WeatherMetricCard("UV Index", "6 (High)", Icons.Default.WbSunny, BrandAmber, Modifier.weight(1f))
                        WeatherMetricCard("Visibility", "10 km", Icons.Default.Visibility, InfoBlue, Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(Spacing.xl))
                    SectionHeader(title = "7-Day Forecast", actionText = "Details")
                    SevenDayForecastList()
                    
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
fun HourlyForecastStrip() {
    val hours = (9..20).map { if (it > 12) "${it-12} PM" else "$it AM" }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
        items(hours) { hour ->
            val isSelected = hour == "10 AM"
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clip(ShapeM)
                    .background(if (isSelected) BrandGreen else DarkSurface2)
                    .border(1.dp, if (isSelected) BrandGreenGlow else DarkBorder, ShapeM)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(hour, style = TypographyTokens.Micro, color = if (isSelected) Color.White else DarkTextSub)
                Spacer(Modifier.height(Spacing.s))
                Icon(Icons.Default.Cloud, null, modifier = Modifier.size(20.dp), tint = if (isSelected) Color.White else BrandSky)
                Spacer(Modifier.height(Spacing.s))
                Text("28°", style = TypographyTokens.BodyM, fontWeight = FontWeight.Bold)
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
            Text(value, style = TypographyTokens.HeadingS)
        }
    }
}

@Composable
fun SevenDayForecastList() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
        days.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeM)
                    .background(DarkSurface2)
                    .padding(horizontal = Spacing.md, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(day, modifier = Modifier.width(48.dp), style = TypographyTokens.BodyM)
                Icon(Icons.Default.WbCloudy, null, tint = DarkTextSub, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Spacing.m))
                Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(DarkBorder)) {
                    Box(modifier = Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(BrandSky))
                }
                Spacer(Modifier.width(Spacing.m))
                Text("30°", style = TypographyTokens.BodyM, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(Spacing.s))
                Text("22°", style = TypographyTokens.BodyM, color = DarkTextSub)
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
