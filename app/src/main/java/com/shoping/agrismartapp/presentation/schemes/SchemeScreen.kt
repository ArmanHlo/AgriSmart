package com.shoping.agrismartapp.presentation.schemes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismartapp.domain.model.GovernmentScheme
import com.shoping.agrismartapp.presentation.theme.*

@Composable
fun SchemeScreen(
    onBack: () -> Unit,
    viewModel: SchemeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SchemeHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.m),
                    placeholder = { Text("Search Schemes (PM-Kisan...)", style = TypographyTokens.BodyM, color = DarkTextSub) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = BrandGreenGlow) },
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

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                    contentPadding = PaddingValues(vertical = Spacing.s)
                ) {
                    items(listOf("All", "Financial", "Insurance", "Subsidies", "Training")) { category ->
                        PremiumChip(label = category, selected = category == "All") { }
                    }
                }

                Spacer(Modifier.height(Spacing.m))

                if (state.isLoading) {
                    repeat(3) { ShimmerBox(width = 400.dp, height = 180.dp, modifier = Modifier.padding(bottom = Spacing.m)) }
                } else {
                    val filteredSchemes = state.schemes.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.m),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredSchemes) { scheme ->
                            PremiumSchemeCard(scheme)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SchemeHeader(onBack: () -> Unit) {
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
        Text("Govt. Schemes", style = TypographyTokens.HeadingL)
    }
}

@Composable
fun PremiumSchemeCard(scheme: GovernmentScheme) {
    val context = LocalContext.current
    
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.asBrush()
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(BrandAmber.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalance, null, tint = BrandAmber, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(scheme.name, style = TypographyTokens.HeadingS, color = Color.White)
                    Text(scheme.ministry, style = TypographyTokens.Micro, color = DarkTextSub)
                }
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            GlassCard(tintColor = DarkSurface2.copy(0.5f)) {
                Column {
                    Text("BENEFIT", style = TypographyTokens.Label, color = BrandGreenGlow)
                    Text(scheme.benefit, style = TypographyTokens.BodyS, color = DarkText)
                }
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = BrandSky, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Check Eligibility", style = TypographyTokens.Micro, color = BrandSky)
                }
                
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.applyLink))
                        context.startActivity(intent)
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = BrandGreenLight.copy(0.1f))
                ) {
                    Text("APPLY NOW", style = TypographyTokens.Label, color = BrandGreenLight)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Launch, null, modifier = Modifier.size(12.dp), tint = BrandGreenLight)
                }
            }
        }
    }
}
