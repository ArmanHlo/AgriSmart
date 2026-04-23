package com.shoping.agrismart.presentation.irrigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IrrigationScreen(
    onBack: () -> Unit,
    viewModel: IrrigationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.md)
        ) {
            Spacer(Modifier.height(Spacing.huge))
            
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(DarkSurface2)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(Spacing.m))
                Text("Smart Irrigation", style = TypographyTokens.HeadingL)
            }

            Spacer(Modifier.height(Spacing.xl))

            SectionHeader(title = "Calculate Needs", subtitle = "Precision water management")

            // Crop Selector
            Text("Select Crop", style = TypographyTokens.HeadingS, modifier = Modifier.padding(bottom = Spacing.s))
            val crops = listOf("Wheat", "Rice (Paddy)", "Cotton", "Sugarcane", "Maize")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                crops.forEach { crop ->
                    PremiumChip(
                        label = crop,
                        selected = state.selectedCrop == crop,
                        onToggle = { viewModel.onCropSelected(crop) }
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            // Soil Selector
            Text("Soil Type", style = TypographyTokens.HeadingS, modifier = Modifier.padding(bottom = Spacing.s))
            val soils = listOf("Alluvial", "Black", "Red", "Loamy", "Clay", "Sandy")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                soils.forEach { soil ->
                    PremiumChip(
                        label = soil,
                        selected = state.selectedSoil == soil,
                        onToggle = { viewModel.onSoilSelected(soil) }
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            // Farm Area
            Text("Farm Area (Acres)", style = TypographyTokens.HeadingS, modifier = Modifier.padding(bottom = Spacing.s))
            OutlinedTextField(
                value = state.farmArea,
                onValueChange = { viewModel.onAreaChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TypographyTokens.BodyL,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreenLight,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = BrandGreenLight
                ),
                shape = ShapeM
            )

            Spacer(Modifier.height(Spacing.xxl))

            AnimatedVisibility(
                visible = state.recommendedLiters > 0,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                IrrigationResultCard(state)
            }
            
            Spacer(Modifier.height(Spacing.huge))
        }
    }
}

@Composable
fun IrrigationResultCard(state: IrrigationState) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = GradientSky,
        glowColor = BrandSky.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Text(
                "Recommended Water",
                style = TypographyTokens.HeadingS,
                color = Color.White.copy(0.8f)
            )
            
            AnimatedCounter(
                target = state.recommendedLiters.toInt(),
                unit = " Liters",
                style = TypographyTokens.DisplayM,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(Spacing.s))
            
            StatusPill(text = "Frequency: ${state.frequency}", type = StatusType.INFO)
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            GlassCard(tintColor = Color.White.copy(0.1f)) {
                Text(
                    "💡 Tip: Water early morning or late evening to minimize evaporation.",
                    style = TypographyTokens.BodyS,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
