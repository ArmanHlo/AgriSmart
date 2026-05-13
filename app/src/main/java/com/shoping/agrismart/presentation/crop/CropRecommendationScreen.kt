@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
package com.shoping.agrismart.presentation.crop

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.domain.model.Crop
import com.shoping.agrismart.presentation.theme.*

@Composable
fun CropRecommendationScreen(
    onBack: () -> Unit,
    viewModel: CropRecommendationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(Spacing.md)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(DarkSurface2)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(Spacing.m))
                Text("Crop Advisor", style = TypographyTokens.HeadingL, color = Color.White)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(BrandGreen.copy(alpha = 0.15f), Color.Transparent),
                            radius = 2000f
                        )
                    )
            )

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md)) {
                // Step Indicator
                if (state.currentStep <= 5) {
                    Spacer(Modifier.height(Spacing.m))
                    StepIndicator(currentStep = state.currentStep)
                    Spacer(Modifier.height(Spacing.xxl))
                }

                // Content Area with Transition
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                            } else {
                                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                            }.using(SizeTransform(clip = false))
                        },
                        label = "step_transition"
                    ) { step ->
                        when (step) {
                            1 -> LocationStep(state.selectedLocation, viewModel::onLocationSelected)
                            2 -> SoilStep(state.selectedSoilType, viewModel::onSoilTypeSelected)
                            3 -> SeasonStep(state.selectedSeason, viewModel::onSeasonSelected)
                            4 -> WaterStep(state.waterSource, viewModel::onWaterSourceSelected)
                            5 -> BudgetStep(state.budget, viewModel::onBudgetChanged)
                            else -> RecommendationResults(state)
                        }
                    }
                }

                // Navigation Buttons
                if (state.currentStep <= 5) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = Spacing.xl),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        if (state.currentStep > 1) {
                            OutlinedButton(
                                onClick = viewModel::previousStep,
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = ShapePill,
                                border = BorderStroke(1.dp, DarkBorder)
                            ) {
                                Text("Back", style = TypographyTokens.HeadingS, color = DarkTextSub)
                            }
                        }
                        
                        GlowButton(
                            text = if (state.currentStep == 5) "Get Analysis" else "Next",
                            modifier = Modifier.weight(2f),
                            onClick = viewModel::nextStep,
                            enabled = when(state.currentStep) {
                                1 -> state.selectedLocation.isNotEmpty()
                                2 -> state.selectedSoilType.isNotEmpty()
                                3 -> state.selectedSeason.isNotEmpty()
                                4 -> state.waterSource.isNotEmpty()
                                else -> true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val step = index + 1
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep
            
            // Node
            Box(
                modifier = Modifier
                    .size(if (isCurrent) 32.dp else 24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted || isCurrent) BrandGreen else DarkSurface2)
                    .border(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = if (isCurrent) Color.White else if (isCompleted) BrandGreen else DarkBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = Color.White)
                } else {
                    Text(
                        step.toString(),
                        style = TypographyTokens.Micro,
                        color = if (isCurrent) Color.White else DarkTextSub
                    )
                }
            }
            
            // Line
            if (index < 4) {
                val lineColor by animateColorAsState(
                    if (step < currentStep) BrandGreen else DarkBorder,
                    label = "lineColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(lineColor)
                )
            }
        }
    }
}

@Composable
fun LocationStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = "Where is your farm?",
        subtitle = "Climate vary significantly by region.",
        icon = Icons.Default.LocationOn
    ) {
        val states = listOf("Punjab", "Haryana", "UP", "Maharashtra", "Gujarat", "Karnataka", "Bihar", "MP")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
            modifier = Modifier.height(300.dp)
        ) {
            items(states) { state ->
                SelectableOptionCard(
                    label = state,
                    selected = selected == state,
                    onClick = { onSelect(state) }
                )
            }
        }
    }
}

@Composable
fun SoilStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = "Select Soil Type",
        subtitle = "Critical factor for nutrient intake.",
        icon = Icons.Default.Terrain
    ) {
        val soils = listOf(
            SoilOption("Alluvial", Color(0xFFD2B48C)),
            SoilOption("Black", Color(0xFF1A1A1A)),
            SoilOption("Red", Color(0xFFB22222)),
            SoilOption("Clay", Color(0xFF8B4513)),
            SoilOption("Sandy", Color(0xFFF4A460)),
            SoilOption("Loamy", Color(0xFF556B2F))
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
            modifier = Modifier.height(260.dp)
        ) {
            items(soils) { soil ->
                Column(
                    modifier = Modifier
                        .clip(ShapeM)
                        .clickable { onSelect(soil.name) }
                        .background(if (selected == soil.name) BrandGreen.copy(0.2f) else DarkSurface2)
                        .border(1.dp, if (selected == soil.name) BrandGreenGlow else DarkBorder, ShapeM)
                        .padding(Spacing.s),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(soil.color))
                    Spacer(Modifier.height(Spacing.xs))
                    Text(soil.name, style = TypographyTokens.Micro, color = Color.White)
                }
            }
        }
    }
}

data class SoilOption(val name: String, val color: Color)

@Composable
fun SeasonStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = "Sowing Season",
        subtitle = "Match your timing with the weather.",
        icon = Icons.Default.WbSunny
    ) {
        val seasons = listOf("Kharif (Monsoon)", "Rabi (Winter)", "Zaid (Summer)")
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
            seasons.forEach { season ->
                SelectableOptionCard(
                    label = season,
                    selected = selected == season,
                    onClick = { onSelect(season) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun WaterStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = "Water Source",
        subtitle = "Irrigation capacity defines yield.",
        icon = Icons.Default.WaterDrop
    ) {
        val sources = listOf("Rainfed", "Canal", "Borewell", "Drip Irrigation", "Other")
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
            sources.forEach { source ->
                SelectableOptionCard(
                    label = source,
                    selected = selected == source,
                    onClick = { onSelect(source) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BudgetStep(budget: Float, onBudgetChanged: (Float) -> Unit) {
    StepCard(
        title = "What is your budget?",
        subtitle = "Estimated cost per acre (in ₹).",
        icon = Icons.Default.Payments
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "₹${budget.toInt()}",
                style = TypographyTokens.HeadingL,
                color = BrandGreenGlow
            )
            Spacer(Modifier.height(Spacing.m))
            Slider(
                value = budget,
                onValueChange = onBudgetChanged,
                valueRange = 5000f..100000f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = BrandGreenGlow,
                    activeTrackColor = BrandGreen,
                    inactiveTrackColor = DarkSurface2
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("₹5k", style = TypographyTokens.Micro, color = DarkTextSub)
                Text("₹100k", style = TypographyTokens.Micro, color = DarkTextSub)
            }
        }
    }
}

@Composable
fun RecommendationResults(state: CropRecommendationState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Top Picks for Your Farm", style = TypographyTokens.HeadingM, color = Color.White)
        Text("Optimal crops based on your conditions", style = TypographyTokens.BodyS, color = DarkTextSub)
        
        Spacer(Modifier.height(Spacing.m))
        
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreenGlow)
            }
        } else if (state.recommendations.isEmpty()) {
            EmptyStateView(
                lottieRes = 0, // Placeholder
                title = "No matches found",
                subtitle = "Try adjusting your filters for better results."
            )
        } else {
            val topPick = state.recommendations.first()
            val otherPicks = state.recommendations.drop(1)

            // Hero Card for #1 Position
            KrishiCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.l),
                gradient = GradientGreen,
                glowColor = BrandGreenGlow.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.xl),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        StatusPill(text = "Best Match", type = StatusType.SUCCESS)
                        Spacer(Modifier.height(8.dp))
                        Text(topPick.name, style = TypographyTokens.DisplayM, color = Color.White)
                        Text(topPick.type, style = TypographyTokens.BodyM, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(Spacing.m))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("High Yield Potential", style = TypographyTokens.Micro, color = Color.White)
                        }
                    }
                    
                    CropScoreGauge(score = topPick.matchScore, size = 100.dp)
                }
            }

            if (state.aiAdvice != null) {
                KrishiCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.m),
                    gradient = Brush.linearGradient(listOf(Color(0xFF1A237E), Color(0xFF311B92))),
                    glowColor = Color(0x336200EA)
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, "AI", tint = BrandAmber, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("AI INSIGHT", style = TypographyTokens.Label, color = BrandAmber)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(state.aiAdvice, style = TypographyTokens.BodyS, color = Color.White)
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
                items(otherPicks) { crop ->
                    KrishiCard(
                        modifier = Modifier.fillMaxWidth(),
                        gradient = DarkSurface2.toBrush()
                    ) {
                        Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(60.dp).background(BrandGreenLight.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Grass, null, tint = BrandGreenLight)
                            }
                            Spacer(Modifier.width(Spacing.m))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(crop.name, style = TypographyTokens.HeadingS, color = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = crop.type,
                                        style = TypographyTokens.Micro,
                                        color = BrandAmber,
                                        modifier = Modifier
                                            .background(BrandAmber.copy(0.1f), ShapePill)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text("Matches your ${state.selectedSoilType} soil", style = TypographyTokens.Micro, color = SuccessGreen)
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    StatusPill(text = crop.season, type = StatusType.INFO)
                                    StatusPill(text = "${crop.matchScore}% Match", type = StatusType.SUCCESS)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = DarkTextSub)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush()
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BrandGreenLight.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = BrandGreenLight)
                }
                Spacer(Modifier.width(Spacing.m))
                Column {
                    Text(title, style = TypographyTokens.HeadingS, color = Color.White)
                    Text(subtitle, style = TypographyTokens.BodyS, color = DarkTextSub)
                }
            }
            Spacer(Modifier.height(Spacing.xl))
            content()
        }
    }
}

@Composable
fun SelectableOptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(ShapeM)
            .background(if (selected) BrandGreen.copy(0.2f) else DarkSurface2)
            .border(1.dp, if (selected) BrandGreenGlow else DarkBorder, ShapeM)
            .clickable { onClick() }
            .padding(Spacing.m),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = TypographyTokens.BodyM,
            color = if (selected) Color.White else DarkTextSub
        )
    }
}
