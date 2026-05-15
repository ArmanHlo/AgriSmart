package com.shoping.agrismart.presentation.market

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.domain.model.MarketPrice
import com.shoping.agrismart.presentation.theme.*

import androidx.compose.ui.res.stringResource
import com.shoping.agrismart.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceScreen(
    onBack: () -> Unit,
    viewModel: MarketPriceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            MarketHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.m),
                    placeholder = { Text(stringResource(R.string.search_placeholder), style = TypographyTokens.BodyM, color = DarkTextSub) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = BrandGreenGlow) },
                    trailingIcon = { 
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, null, tint = DarkTextSub)
                            }
                        } else {
                            Icon(Icons.Default.FilterList, null, tint = DarkTextSub)
                        }
                    },
                    shape = ShapePill,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface2,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = BrandGreenLight,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Market Chart Preview
                MarketPriceChart()

                Spacer(Modifier.height(Spacing.m))
                
                SectionHeader(
                    title = stringResource(R.string.live_mandi_rates), 
                    subtitle = stringResource(R.string.updated_5_mins_ago)
                )

                if (state.isLoading) {
                    repeat(5) {
                        ShimmerBox(width = 400.dp, height = 100.dp, modifier = Modifier.padding(bottom = Spacing.m))
                    }
                } else {
                    // Local filter for instant responsiveness on already loaded data
                    val filteredPrices = state.prices.filter {
                        it.commodity.contains(state.searchQuery, ignoreCase = true) ||
                        it.market.contains(state.searchQuery, ignoreCase = true) ||
                        it.district.contains(state.searchQuery, ignoreCase = true)
                    }

                    if (filteredPrices.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(64.dp), tint = DarkTextSub.copy(alpha = 0.5f))
                            Spacer(Modifier.height(Spacing.m))
                            Text("No matches found", style = TypographyTokens.HeadingM, color = Color.White)
                            Text("Try searching for a different commodity or market", style = TypographyTokens.BodyS, color = DarkTextSub)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.m),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(filteredPrices) { price ->
                                MarketPriceCardPremium(price = price)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketHeader(onBack: () -> Unit) {
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
        Text(stringResource(R.string.market_pulse_header), style = TypographyTokens.HeadingL, color = Color.White)
        Spacer(Modifier.weight(1f))
        StatusPill(text = stringResource(R.string.live), type = StatusType.SUCCESS)
    }
}

@Composable
fun MarketPriceChart() {
    KrishiCard(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        gradient = GradientNight
    ) {
        // Simple Placeholder for Chart
        Box(modifier = Modifier.fillMaxSize().padding(Spacing.md)) {
            Column {
                Text(stringResource(R.string.price_trend_wheat), style = TypographyTokens.Label, color = DarkTextSub)
                Text("₹2,250", style = TypographyTokens.HeadingL, color = BrandGreenGlow)
            }
            // Simulating a mini sparkline
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, size.height * 0.8f)
                    lineTo(size.width * 0.2f, size.height * 0.7f)
                    lineTo(size.width * 0.4f, size.height * 0.75f)
                    lineTo(size.width * 0.6f, size.height * 0.5f)
                    lineTo(size.width * 0.8f, size.height * 0.4f)
                    lineTo(size.width, size.height * 0.2f)
                }
                drawPath(path, color = BrandGreenGlow, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                
                // Gradient Fill
                val fillPath = path.apply {
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(fillPath, brush = Brush.verticalGradient(listOf(BrandGreenGlow.copy(0.2f), Color.Transparent)))
            }
        }
    }
}

@Composable
fun MarketPriceCardPremium(price: MarketPrice) {
    val isUp = (0..1).random() == 1 // Simulating trend
    
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush(),
        onClick = { /* Handle click if needed, or just let it have the click effect */ }
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Commodity Icon/Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BrandGreen.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(price.commodity.take(1), style = TypographyTokens.HeadingM, color = BrandGreenGlow)
            }
            
            Spacer(Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(price.commodity, style = TypographyTokens.HeadingS, color = Color.White)
                Text("${price.market}, ${price.district}", style = TypographyTokens.Micro, color = DarkTextSub)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${price.modalPrice}", style = TypographyTokens.DataNum, color = Color.White)
                Text("per ${price.unit}", style = TypographyTokens.Micro, color = DarkTextSub)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = if (isUp) SuccessGreen else DangerRed
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        if (isUp) "+2.4%" else "-0.8%",
                        style = TypographyTokens.Micro,
                        color = if (isUp) SuccessGreen else DangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
