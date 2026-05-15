@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
package com.shoping.agrismartapp.presentation.crop

import com.shoping.agrismartapp.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismartapp.presentation.navigation.Screen
import com.shoping.agrismartapp.presentation.theme.*

import androidx.compose.ui.res.stringResource

@Composable
fun CropRecommendationScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
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
                Text(stringResource(R.string.crop_advisor), style = TypographyTokens.HeadingL, color = Color.White)
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

            // Content Area
            if (state.currentStep <= 5) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md)) {
                    // Step Indicator
                    Spacer(Modifier.height(Spacing.m))
                    StepIndicator(currentStep = state.currentStep)
                    Spacer(Modifier.height(Spacing.xxl))

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
                                else -> Box(Modifier) // Should not happen
                            }
                        }
                    }

                    // Navigation Buttons
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
                                Text(stringResource(R.string.back), style = TypographyTokens.HeadingS, color = DarkTextSub)
                            }
                        }
                        
                        GlowButton(
                            text = if (state.currentStep == 5) stringResource(R.string.get_analysis) else stringResource(R.string.next),
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
            } else {
                // Results Area - Fixed scrolling by making it a single LazyColumn
                RecommendationResults(state, onNavigate)
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
        title = stringResource(R.string.where_is_your_farm),
        subtitle = stringResource(R.string.climate_vary_subtitle),
        icon = Icons.Default.LocationOn
    ) {
        val statesMap = listOf(
            "Punjab" to R.string.punjab,
            "Haryana" to R.string.haryana,
            "UP" to R.string.up,
            "Maharashtra" to R.string.maharashtra,
            "Gujarat" to R.string.gujarat,
            "Karnataka" to R.string.karnataka,
            "Bihar" to R.string.bihar,
            "MP" to R.string.mp,
            "Rajasthan" to R.string.rajasthan,
            "West Bengal" to R.string.west_bengal,
            "Andhra Pradesh" to R.string.andhra_pradesh,
            "Telangana" to R.string.telangana,
            "Tamil Nadu" to R.string.tamil_nadu,
            "Odisha" to R.string.odisha,
            "Kerala" to R.string.kerala,
            "Assam" to R.string.assam,
            "Chhattisgarh" to R.string.chhattisgarh,
            "Jharkhand" to R.string.jharkhand,
            "Uttarakhand" to R.string.uttarakhand,
            "Himachal Pradesh" to R.string.himachal_pradesh,
            "Other" to R.string.other
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
            modifier = Modifier.height(400.dp)
        ) {
            items(statesMap) { statePair ->
                SelectableOptionCard(
                    label = stringResource(statePair.second),
                    selected = selected == statePair.first,
                    onClick = { onSelect(statePair.first) }
                )
            }
        }
    }
}

@Composable
fun SoilStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = stringResource(R.string.select_soil_type),
        subtitle = stringResource(R.string.soil_type_subtitle),
        icon = Icons.Default.Terrain
    ) {
        val soils = listOf(
            SoilOption("Alluvial", R.string.alluvial, Color(0xFFD2B48C)),
            SoilOption("Black", R.string.black, Color(0xFF1A1A1A)),
            SoilOption("Red", R.string.red, Color(0xFFB22222)),
            SoilOption("Clay", R.string.clay, Color(0xFF8B4513)),
            SoilOption("Sandy", R.string.sandy, Color(0xFFF4A460)),
            SoilOption("Loamy", R.string.loamy, Color(0xFF556B2F))
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
                    Text(stringResource(soil.labelRes), style = TypographyTokens.Micro, color = Color.White)
                }
            }
        }
    }
}

data class SoilOption(val name: String, val labelRes: Int, val color: Color)

@Composable
fun SeasonStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = stringResource(R.string.sowing_season),
        subtitle = stringResource(R.string.sowing_season_subtitle),
        icon = Icons.Default.WbSunny
    ) {
        val seasons = listOf(
            "Kharif (Monsoon)" to R.string.kharif,
            "Rabi (Winter)" to R.string.rabi,
            "Zaid (Summer)" to R.string.zaid
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
            seasons.forEach { seasonPair ->
                SelectableOptionCard(
                    label = stringResource(seasonPair.second),
                    selected = selected == seasonPair.first,
                    onClick = { onSelect(seasonPair.first) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun WaterStep(selected: String, onSelect: (String) -> Unit) {
    StepCard(
        title = stringResource(R.string.water_source),
        subtitle = stringResource(R.string.water_source_subtitle),
        icon = Icons.Default.WaterDrop
    ) {
        val sources = listOf(
            "Rainfed" to R.string.rainfed,
            "Canal" to R.string.canal,
            "Borewell" to R.string.borewell,
            "Drip Irrigation" to R.string.drip_irrigation,
            "Other" to R.string.other
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.m)) {
            sources.forEach { sourcePair ->
                SelectableOptionCard(
                    label = stringResource(sourcePair.second),
                    selected = selected == sourcePair.first,
                    onClick = { onSelect(sourcePair.first) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BudgetStep(budget: Float, onBudgetChanged: (Float) -> Unit) {
    StepCard(
        title = stringResource(R.string.what_is_your_budget),
        subtitle = stringResource(R.string.budget_subtitle),
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
fun translateSeason(season: String): String {
    return when (season) {
        "Kharif" -> stringResource(R.string.kharif)
        "Rabi" -> stringResource(R.string.rabi)
        "Zaid" -> stringResource(R.string.zaid)
        "Annual" -> stringResource(R.string.annual)
        else -> season
    }
}

@Composable
fun translateType(type: String): String {
    return when (type) {
        "Cereal" -> stringResource(R.string.cereal)
        "Fruit" -> stringResource(R.string.fruit)
        "Vegetable" -> stringResource(R.string.vegetable)
        "Fiber" -> stringResource(R.string.fiber)
        "Cash Crop" -> stringResource(R.string.cash_crop)
        else -> type
    }
}

@Composable
fun RecommendationResults(state: CropRecommendationState, onNavigate: (String) -> Unit) {
    val topPick = state.recommendations.firstOrNull()
    val otherPicks = state.recommendations.drop(1)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        contentPadding = PaddingValues(bottom = Spacing.xl)
    ) {
        item {
            Column {
                Text(
                    stringResource(R.string.top_picks_for_your_farm),
                    style = TypographyTokens.HeadingM,
                    color = Color.White
                )
                Text(
                    stringResource(R.string.optimal_crops_subtitle),
                    style = TypographyTokens.BodyS,
                    color = DarkTextSub
                )
                Spacer(Modifier.height(Spacing.m))
            }
        }

        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(Spacing.xl), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreenGlow)
                }
            }
        } else if (state.recommendations.isEmpty()) {
            item {
                EmptyStateView(
                    lottieRes = 0, // Placeholder
                    title = stringResource(R.string.no_matches_found),
                    subtitle = stringResource(R.string.adjust_filters_subtitle)
                )
            }
        } else {
            // Hero Card for #1 Position
            topPick?.let { crop ->
                item {
                    KrishiCard(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.s),
                        gradient = GradientGreen,
                        glowColor = BrandGreenGlow.copy(alpha = 0.4f),
                        onClick = { onNavigate(Screen.KrishiBot.route + "?prompt=Tell me more about ${crop.name} cultivation") }
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.xl),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                StatusPill(text = stringResource(R.string.best_match), type = StatusType.SUCCESS)
                                Spacer(Modifier.height(8.dp))
                                Text(crop.name, style = TypographyTokens.DisplayM, color = Color.White)
                                Text(translateType(crop.type), style = TypographyTokens.BodyM, color = Color.White.copy(alpha = 0.8f))
                                Spacer(Modifier.height(Spacing.m))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(stringResource(R.string.high_yield_potential), style = TypographyTokens.Micro, color = Color.White)
                                }
                            }
                            CropScoreGauge(score = crop.matchScore, size = 100.dp)
                        }
                    }
                }
            }

            if (state.aiAdvice != null) {
                item {
                    KrishiCard(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.s),
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
            }

            items(otherPicks) { crop ->
                KrishiCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradient = DarkSurface2.toBrush(),
                    onClick = { onNavigate(Screen.KrishiBot.route + "?prompt=Tell me more about ${crop.name} cultivation") }
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
                                    text = translateType(crop.type),
                                    style = TypographyTokens.Micro,
                                    color = BrandAmber,
                                    modifier = Modifier
                                        .background(BrandAmber.copy(0.1f), ShapePill)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(stringResource(R.string.optimal_conditions), style = TypographyTokens.Micro, color = SuccessGreen)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusPill(text = translateSeason(crop.season), type = StatusType.INFO)
                                StatusPill(text = stringResource(R.string.match_score_percentage, crop.matchScore), type = StatusType.SUCCESS)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = DarkTextSub)
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
