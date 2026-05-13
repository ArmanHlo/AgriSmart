package com.shoping.agrismart.presentation.soil

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shoping.agrismart.presentation.theme.*

import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SoilHealthScreen(
    onBack: () -> Unit,
    viewModel: SoilHealthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SoilHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                // Soil Hero Card
                KrishiCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradient = GradientEarth,
                    glowColor = BrandEarth.copy(alpha = 0.3f)
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
                            Icon(Icons.Default.Science, null, modifier = Modifier.size(32.dp), tint = Color.White)
                        }
                        Spacer(Modifier.width(Spacing.m))
                        Column {
                            Text("Soil Analysis", style = TypographyTokens.HeadingM, color = Color.White)
                            Text("Enter lab results for precise advice", style = TypographyTokens.BodyS, color = Color.White.copy(0.8f))
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.xl))
                
                SectionHeader(title = "Parameters", subtitle = "N-P-K & pH levels")

                // pH Level Input
                KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface.asBrush()) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("pH Level", style = TypographyTokens.BodyM, color = DarkText)
                            Text(if (state.ph.isEmpty()) "7.0" else state.ph, style = TypographyTokens.HeadingS, color = BrandGreenGlow)
                        }
                        Spacer(Modifier.height(Spacing.s))
                        // Simulated pH gradient bar
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(ShapePill).background(
                            Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue))
                        ))
                        Spacer(Modifier.height(Spacing.m))
                        OutlinedTextField(
                            value = state.ph,
                            onValueChange = viewModel::onPhChange,
                            placeholder = { Text("Enter pH (0-14)", color = DarkTextSub.copy(0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = ShapeM,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandGreenLight)
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.md))

                // NPK Inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    NPKInput("Nitrogen (N)", state.nitrogen, Modifier.weight(1f), viewModel::onNitrogenChange)
                    NPKInput("Phosphorus (P)", state.phosphorus, Modifier.weight(1f), viewModel::onPhosphorusChange)
                    NPKInput("Potassium (K)", state.potassium, Modifier.weight(1f), viewModel::onPotassiumChange)
                }

                Spacer(Modifier.height(Spacing.xl))

                GlowButton(
                    text = "Generate Soil Health Report",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::analyzeSoil
                )

                if (state.result != null) {
                    Spacer(Modifier.height(Spacing.xl))
                    SectionHeader(title = "Results", subtitle = state.result!!)
                    
                    state.recommendations.forEach { recommendation ->
                        SoilTipCard(recommendation, BrandGreenGlow)
                    }
                    
                    Spacer(Modifier.height(Spacing.m))
                    TextButton(onClick = viewModel::reset, modifier = Modifier.fillMaxWidth()) {
                        Text("Clear Results", color = DangerRed)
                    }
                } else {
                    Spacer(Modifier.height(Spacing.xl))
                    SectionHeader(title = "Improvement Tips")
                    
                    SoilTipCard("Use organic mulch to retain moisture and nutrients.", SuccessGreen)
                    SoilTipCard("Consider green manuring with legumes before sowing.", BrandSky)
                    SoilTipCard("Add gypsum if soil pH is too alkaline (>8.5).", WarningAmber)
                }

                Spacer(Modifier.height(Spacing.huge))
            }
        }
    }
}

@Composable
fun SoilHeader(onBack: () -> Unit) {
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
        Text("Soil Health", style = TypographyTokens.HeadingL)
    }
}

@Composable
fun NPKInput(label: String, value: String, modifier: Modifier, onValueChange: (String) -> Unit) {
    KrishiCard(modifier = modifier, gradient = DarkSurface.asBrush()) {
        Column(modifier = Modifier.padding(Spacing.m), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label.take(1), style = TypographyTokens.HeadingM, color = BrandGreenGlow)
            Text(label.substringAfter("(").substringBefore(")"), style = TypographyTokens.Micro, color = DarkTextSub)
            Spacer(Modifier.height(Spacing.s))
            OutlinedTextField(
                value = value,
                onValueChange = { if (it.all { c -> c.isDigit() }) onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TypographyTokens.BodyM.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                shape = ShapeS,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandGreenLight)
            )
        }
    }
}

@Composable
fun SoilTipCard(tip: String, color: Color) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.m),
        gradient = DarkSurface2.asBrush()
    ) {
        Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(Modifier.width(Spacing.m))
            Text(tip, style = TypographyTokens.BodyS, color = DarkText)
        }
    }
}
