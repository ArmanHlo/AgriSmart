package com.shoping.agrismart.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
                SectionHeader(title = "Quick Actions", subtitle = "Tools for your farm", titleColor = Color.White)
                QuickActionsGrid(onNavigate)
                
                Spacer(Modifier.height(Spacing.xl))
                SectionHeader(title = "AI Advisor", actionText = "See All", onAction = {}, titleColor = Color.White)
                CropAdvisorPreview()
                
                Spacer(Modifier.height(Spacing.xl))
                MarketPulseCard()
                
                Spacer(Modifier.height(Spacing.xl))
                SectionHeader(title = "Farm Health", subtitle = "Based on recent data", titleColor = Color.White)
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
                        text = "Good Morning 🌱",
                        style = TypographyTokens.BodyM,
                        color = DarkTextSub
                    )
                    Text(
                        text = if (user?.name.isNullOrBlank()) "Farmer" else user?.name!!,
                        style = TypographyTokens.DisplayM,
                        color = BrandGreenGlow
                    )
                    Text(
                        text = "${if (user?.location.isNullOrBlank()) "Location not set" else user?.location} • ${if (user?.farmSize.isNullOrBlank()) "0" else user?.farmSize} acres",
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
                    Text("Feels like ${weather?.main?.feels_like?.toInt() ?: "--"}°C", style = TypographyTokens.BodyS, color = Color.White.copy(0.7f))
                    Text("${weather?.weather?.firstOrNull()?.description ?: "Updating..."} · ${weather?.name ?: "Your Farm"}", style = TypographyTokens.BodyM, color = Color.White)
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
                WeatherMetricPill("🌬 12km/h")
                WeatherMetricPill("☀️ UV 6")
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            StatusPill(
                text = if ((weather?.main?.humidity ?: 0) > 70) "⚠️ High humidity — fungal risk today" else "✅ Optimal conditions for farm activities",
                type = if ((weather?.main?.humidity ?: 0) > 70) StatusType.WARNING else StatusType.SUCCESS
            )
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
        HomeAction("Crop AI", Icons.Default.Agriculture, GradientGreen, Screen.CropAdvisor.route),
        HomeAction("Scan", Icons.Default.CameraAlt, Brush.linearGradient(listOf(Color(0xFF006064), Color(0xFF00BCD4))), Screen.DiseaseScanner.route),
        HomeAction("ChatBot", Icons.Default.Chat, Brush.linearGradient(listOf(Color(0xFF4A148C), Color(0xFFAB47BC))), Screen.KrishiBot.route),
        HomeAction("Mandi", Icons.Default.Storefront, GradientAmber, Screen.MarketPrices.route),
        HomeAction("Weather", Icons.Default.Cloud, GradientSky, Screen.Weather.route),
        HomeAction("Soil", Icons.Default.Science, GradientEarth, Screen.SoilHealth.route),
        HomeAction("Schemes", Icons.Default.Description, Brush.linearGradient(listOf(Color(0xFF1A231E), Color(0xFF5C6BC0))), Screen.GovtSchemes.route),
        HomeAction("Journal", Icons.Default.Book, Brush.linearGradient(listOf(Color(0xFF33691E), Color(0xFF8BC34A))), Screen.FarmJournal.route),
        HomeAction("Shop", Icons.Default.ShoppingCart, Brush.linearGradient(listOf(Color(0xFF880E4F), Color(0xFFE91E63))), Screen.MarketPrices.route)
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
            Text("📈 Market Pulse", style = TypographyTokens.HeadingS, color = Color.White)
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
            NavItem(Icons.Default.Home, "Home", currentRoute == Screen.Home.route) { onNavigate(Screen.Home.route) }
            NavItem(Icons.Default.Explore, "Explore", currentRoute == Screen.Community.route) { onNavigate(Screen.Community.route) }
            
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
            
            NavItem(Icons.Default.BarChart, "Market", currentRoute == Screen.MarketPrices.route) { onNavigate(Screen.MarketPrices.route) }
            NavItem(Icons.Default.Person, "Profile", currentRoute == Screen.Profile.route) { onNavigate(Screen.Profile.route) }
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

data class HomeAction(
    val label: String,
    val icon: ImageVector,
    val gradient: Brush,
    val route: String
)
