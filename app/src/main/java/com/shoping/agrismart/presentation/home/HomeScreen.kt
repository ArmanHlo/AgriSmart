package com.shoping.agrismart.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.domain.model.User
import com.shoping.agrismart.presentation.navigation.Screen
import com.shoping.agrismart.presentation.theme.*

import androidx.compose.ui.res.stringResource
import com.shoping.agrismart.R

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            HomeHeader(scrollState, onNavigate, state.user)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                Spacer(Modifier.height(Spacing.m))
                WeatherMegaCard(state.weather)
                
                Spacer(Modifier.height(Spacing.xl))
                ForecastSection(state.weather?.forecast ?: emptyList())

                Spacer(Modifier.height(Spacing.xl))
                SoilIntelligenceCard(state.weather)

                Spacer(Modifier.height(Spacing.xl))
                WeatherTrendsCard()

                Spacer(Modifier.height(Spacing.xl))
                FarmingCalendarCard(state.weather?.forecast ?: emptyList())

                Spacer(Modifier.height(Spacing.xl))
                SectionHeader(
                    title = stringResource(R.string.quick_actions), 
                    subtitle = stringResource(R.string.tools_for_your_farm), 
                    titleColor = Color.White
                )
                QuickActionsGrid(onNavigate)
                
                Spacer(Modifier.height(Spacing.xl))
                SectionHeader(
                    title = stringResource(R.string.ai_advisor), 
                    actionText = stringResource(R.string.see_all), 
                    onAction = {}, 
                    titleColor = Color.White
                )
                CropAdvisorPreview()
                
                Spacer(Modifier.height(Spacing.xl))
                MarketPulseCard()
                
                Spacer(Modifier.height(Spacing.xl))
                SectionHeader(
                    title = stringResource(R.string.farm_health), 
                    subtitle = stringResource(R.string.based_on_recent_data), 
                    titleColor = Color.White
                )
                FarmHealthCard()
                
                Spacer(Modifier.height(Spacing.xxl))
            }
        }

        KrishiBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onNavigate = onNavigate,
            currentRoute = Screen.Home.route
        )
    }
}

@Composable
fun HomeHeader(scrollState: ScrollState, onNavigate: (String) -> Unit, user: User?) {
    val alpha = (1f - (scrollState.value / 400f)).coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .alpha(alpha)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientMesh)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.good_morning),
                        style = TypographyTokens.BodyM,
                        color = DarkTextSub
                    )
                    Text(
                        text = if (user?.name.isNullOrBlank()) stringResource(R.string.farmer) else user?.name!!,
                        style = TypographyTokens.DisplayM,
                        color = BrandGreenGlow
                    )
                    Text(
                        text = "${if (user?.location.isNullOrBlank()) stringResource(R.string.location_not_set) else user?.location} • ${stringResource(R.string.acres, if (user?.farmSize.isNullOrBlank()) "0" else user?.farmSize!!)}",
                        style = TypographyTokens.BodyS,
                        color = DarkTextSub
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.dp, BrandGreenLight, CircleShape)
                        .background(DarkSurface2)
                        .clickable { onNavigate(Screen.Profile.route) }
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        tint = DarkTextSub
                    )
                }
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            Text(
                text = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                style = TypographyTokens.DataNum,
                color = BrandGreenGlow,
                modifier = Modifier.glowShadow(color = BrandGreenGlow.copy(alpha = 0.3f), blurRadius = 12.dp)
            )
        }
    }
}

@Composable
fun WeatherMegaCard(weather: com.shoping.agrismart.data.remote.WeatherResponse?) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = Brush.linearGradient(listOf(Color(0xFF0277BD), Color(0xFF4FC3F7))),
        glowColor = Color(0x330EA5E9)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${weather?.main?.temp?.toInt() ?: "--"}°C", style = TypographyTokens.DisplayL, color = Color.White)
                    Text(stringResource(R.string.feels_like, weather?.main?.feels_like?.toInt() ?: 0), style = TypographyTokens.BodyS, color = Color.White.copy(0.7f))
                    Text("${weather?.weather?.firstOrNull()?.description ?: stringResource(R.string.updating)} · ${weather?.name ?: stringResource(R.string.your_farm)}", style = TypographyTokens.BodyM, color = Color.White)
                }
                
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = BrandAmber
                )
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WeatherMetricPill("💧 ${weather?.main?.humidity ?: "--"}%")
                WeatherMetricPill("🌬 ${weather?.wind?.speed ?: "--"}km/h")
                WeatherMetricPill("☀️ UV ${weather?.uvIndex?.toInt() ?: "--"}")
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            StatusPill(
                text = weather?.advisory?.title ?: stringResource(R.string.optimal_conditions),
                type = when(weather?.advisory?.riskLevel) {
                    "DANGER" -> StatusType.ERROR
                    "WARNING" -> StatusType.WARNING
                    else -> StatusType.SUCCESS
                }
            )
            
            if (weather?.advisory != null) {
                Text(
                    text = weather.advisory.description,
                    style = TypographyTokens.Micro,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WeatherMetricPill(text: String) {
    Box(
        modifier = Modifier
            .clip(ShapeS)
            .background(Color.Black.copy(0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, style = TypographyTokens.Micro, color = Color.White)
    }
}

@Composable
fun QuickActionsGrid(onNavigate: (String) -> Unit) {
    val actions = listOf(
        HomeAction(stringResource(R.string.weather), Icons.Default.Cloud, GradientSky, Screen.Weather.route),
        HomeAction(stringResource(R.string.crop_advisor), Icons.Default.Agriculture, GradientGreen, Screen.CropAdvisor.route),
        HomeAction(stringResource(R.string.farming_calendar), Icons.Default.CalendarToday, Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFFB74D))), Screen.FarmingCalendar.route),
        HomeAction(stringResource(R.string.disease_scan), Icons.Default.CameraAlt, Brush.linearGradient(listOf(Color(0xFF006064), Color(0xFF00BCD4))), Screen.DiseaseScanner.route),
        HomeAction(stringResource(R.string.krishibot), Icons.Default.Chat, Brush.linearGradient(listOf(Color(0xFF4A148C), Color(0xFFAB47BC))), Screen.KrishiBot.route),
        HomeAction(stringResource(R.string.market_price), Icons.AutoMirrored.Filled.TrendingUp, GradientAmber, Screen.MarketPrices.route),
        HomeAction(stringResource(R.string.govt_schemes), Icons.Default.AccountBalance, Brush.linearGradient(listOf(Color(0xFF1A231E), Color(0xFF5C6BC0))), Screen.GovtSchemes.route),
        HomeAction(stringResource(R.string.farm_journal), Icons.Default.Book, Brush.linearGradient(listOf(Color(0xFF33691E), Color(0xFF8BC34A))), Screen.FarmJournal.route),
        HomeAction(stringResource(R.string.irrigation), Icons.Default.WaterDrop, Brush.linearGradient(listOf(Color(0xFF0277BD), Color(0xFF4FC3F7))), Screen.Irrigation.route),
        HomeAction(stringResource(R.string.pest_calendar), Icons.Default.BugReport, Brush.linearGradient(listOf(Color(0xFFE65100), Color(0xFFFFB74D))), Screen.PestCalendar.route),
        HomeAction(stringResource(R.string.my_notes), Icons.Default.Book, Brush.linearGradient(listOf(Color(0xFF33691E), Color(0xFF8BC34A))), Screen.MyNotes.route),
        HomeAction(stringResource(R.string.community), Icons.Default.Groups, Brush.linearGradient(listOf(Color(0xFF4527A0), Color(0xFF7E57C2))), Screen.Community.route)
    )

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
        for (i in actions.indices step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                for (j in 0 until 3) {
                    if (i + j < actions.size) {
                        ActionCard(actions[i + j], onNavigate, Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(action: HomeAction, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KrishiCard(
            modifier = Modifier.aspectRatio(1f),
            gradient = action.gradient,
            glowColor = Color.Black.copy(0.2f),
            onClick = { onNavigate(action.route) }
        ) {
            Icon(
                action.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(action.label.uppercase(), style = TypographyTokens.Label, color = Color.White)
    }
}

@Composable
fun CropAdvisorPreview() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(end = Spacing.md)
    ) {
        items(listOf("Wheat", "Mustard", "Potato")) { crop ->
            KrishiCard(
                modifier = Modifier.width(160.dp),
                gradient = DarkSurface2.toBrush()
            ) {
                Column(modifier = Modifier.padding(Spacing.m)) {
                    Box(modifier = Modifier.size(60.dp).background(BrandGreenLight.copy(0.1f), CircleShape)) {
                        Icon(Icons.Default.Grass, null, Modifier.align(Alignment.Center).size(30.dp), BrandGreenLight)
                    }
                    Spacer(Modifier.height(Spacing.s))
                    Text(crop, style = TypographyTokens.HeadingS, color = Color.White)
                    Text("94% match", style = TypographyTokens.BodyS, color = SuccessGreen)
                    Text("Profit: ₹45k/ac", style = TypographyTokens.Micro, fontFamily = JetBrainsMono, color = DarkTextSub)
                }
            }
        }
    }
}

@Composable
fun MarketPulseCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.market_pulse), style = TypographyTokens.HeadingS, color = Color.White)
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape)) // Pulse
        }
        Spacer(Modifier.height(Spacing.m))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
            items(listOf("Wheat ₹2,250 ▲2.1%", "Rice ₹3,100 ▼0.8%", "Cotton ₹6,500 ▲1.2%")) { pulse ->
                Box(
                    modifier = Modifier
                        .border(1.dp, DarkBorder, ShapePill)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(pulse, style = TypographyTokens.Micro, fontFamily = JetBrainsMono, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FarmHealthCard() {
    KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface.toBrush()) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CropScoreGauge(score = 78, size = 100.dp)
            Column(modifier = Modifier.weight(1f).padding(start = Spacing.md)) {
                HealthMetric("Soil", 0.68f)
                HealthMetric("Water", 0.90f)
                HealthMetric("Weather", 0.82f)
            }
        }
    }
}

@Composable
fun HealthMetric(label: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = TypographyTokens.Micro, color = DarkTextSub)
            Text("${(progress * 100).toInt()}%", style = TypographyTokens.Micro, color = DarkText)
        }
        NeonProgressBar(progress = progress)
    }
}

@Composable
fun KrishiBottomBar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    currentRoute: String?
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(72.dp)
            .fillMaxWidth()
            .glowShadow(color = BrandGreenGlow.copy(alpha = 0.2f)),
        shape = ShapePill,
        color = DarkSurface2.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, stringResource(R.string.home), currentRoute == Screen.Home.route) { onNavigate(Screen.Home.route) }
            NavItem(Icons.Default.Explore, stringResource(R.string.explore), currentRoute == Screen.Community.route) { onNavigate(Screen.Community.route) }
            
            FloatingActionButton(
                onClick = { onNavigate(Screen.DiseaseScanner.route) },
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(GradientGreen, CircleShape)
                        .glowShadow(color = BrandGreenGlow.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CenterFocusStrong, null, tint = Color.White)
                }
            }
            
            NavItem(Icons.Default.BarChart, stringResource(R.string.market), currentRoute == Screen.MarketPrices.route) { onNavigate(Screen.MarketPrices.route) }
            NavItem(Icons.Default.Person, stringResource(R.string.profile), currentRoute == Screen.Profile.route) { onNavigate(Screen.Profile.route) }
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (selected) 1.2f else 0.9f, label = "scale")
    val color = if (selected) BrandGreenGlow else DarkTextSub
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(icon, label, modifier = Modifier.scale(scale), tint = color)
        if (selected) {
            Box(modifier = Modifier.size(4.dp).background(BrandGreenGlow, CircleShape))
        }
    }
}

@Composable
fun SectionHeader(
    title: String, 
    subtitle: String? = null, 
    actionText: String? = null, 
    onAction: (() -> Unit)? = null,
    titleColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(title, style = TypographyTokens.HeadingL, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, style = TypographyTokens.BodyS, color = DarkTextSub)
            }
        }
        if (actionText != null) {
            Text(
                actionText,
                modifier = Modifier.clickable { onAction?.invoke() },
                style = TypographyTokens.Label,
                color = BrandGreenGlow
            )
        }
    }
}

fun Color.toBrush() = Brush.linearGradient(listOf(this, this))

@Composable
fun ForecastSection(forecast: List<com.shoping.agrismart.data.remote.ForecastData>) {
    Column {
        SectionHeader(
            title = stringResource(R.string.seven_day_forecast),
            subtitle = stringResource(R.string.hourly_breakdown_available),
            titleColor = Color.White
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(end = Spacing.md)
        ) {
            items(forecast) { day ->
                KrishiCard(
                    modifier = Modifier.width(100.dp),
                    gradient = DarkSurface2.toBrush()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.m),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(day.date.takeLast(5), style = TypographyTokens.Label, color = DarkTextSub)
                        Spacer(Modifier.height(Spacing.s))
                        Icon(Icons.Default.Cloud, null, Modifier.size(30.dp), Color.White)
                        Spacer(Modifier.height(Spacing.s))
                        Text("${day.tempMax.toInt()}°", style = TypographyTokens.HeadingS, color = Color.White)
                        Text("${day.tempMin.toInt()}°", style = TypographyTokens.BodyS, color = DarkTextSub)
                    }
                }
            }
        }
    }
}

@Composable
fun SoilIntelligenceCard(weather: com.shoping.agrismart.data.remote.WeatherResponse?) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = Brush.linearGradient(listOf(Color(0xFF2E7D32), Color(0xFF81C784))),
        glowColor = Color(0x334CAF50)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(stringResource(R.string.soil_intelligence), style = TypographyTokens.HeadingM, color = Color.White)
            Text(stringResource(R.string.real_time_soil_metrics), style = TypographyTokens.BodyS, color = Color.White.copy(0.7f))
            
            Spacer(Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SoilMetricPill(stringResource(R.string.moisture), "${weather?.soil?.moisture ?: "--"} m³/m³")
                SoilMetricPill(stringResource(R.string.soil_temp), "${weather?.soil?.temperature ?: "--"}°C")
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            Text(
                text = weather?.main?.evapotranspiration?.let {
                    stringResource(R.string.evapotranspiration_value, it)
                } ?: "Evapotranspiration: -- mm",
                style = TypographyTokens.Micro,
                color = Color.White
            )
        }
    }
}

@Composable
fun SoilMetricPill(label: String, value: String) {
    Column {
        Text(label, style = TypographyTokens.Micro, color = Color.White.copy(0.7f))
        Text(value, style = TypographyTokens.HeadingS, color = Color.White)
    }
}

@Composable
fun WeatherTrendsCard() {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush()
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            SectionHeader(
                title = stringResource(R.string.historical_trends),
                subtitle = stringResource(R.string.thirty_day_weather_history),
                titleColor = Color.White
            )
            
            // Placeholder for Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Black.copy(0.2f), ShapeM),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chart Placeholder (MPAndroidChart)",
                    style = TypographyTokens.BodyS,
                    color = DarkTextSub
                )
            }
        }
    }
}

@Composable
fun FarmingCalendarCard(forecast: List<com.shoping.agrismart.data.remote.ForecastData>) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = Brush.linearGradient(listOf(Color(0xFF5D4037), Color(0xFF8D6E63)))
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(stringResource(R.string.farming_calendar), style = TypographyTokens.HeadingM, color = Color.White)
            Text(stringResource(R.string.ai_generated_weekly_schedule), style = TypographyTokens.BodyS, color = Color.White.copy(0.7f))
            
            Spacer(Modifier.height(Spacing.m))
            
            forecast.take(3).forEach { day ->
                val activity = when {
                    day.description.contains("Rain", true) -> "Rest (Rain expected)"
                    day.tempMax > 30 -> "Irrigation recommended"
                    else -> "Fertilizer / Spray window"
                }
                
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(BrandAmber, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text("${day.date.takeLast(5)}: $activity", style = TypographyTokens.BodyM, color = Color.White)
                }
            }
        }
    }
}

data class HomeAction(
    val label: String,
    val icon: ImageVector,
    val gradient: Brush,
    val route: String
)
