package com.shoping.agrismart.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shoping.agrismart.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(page)
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Spacing.xl)
        ) {
            // Page Indicator
            Row(
                Modifier.fillMaxWidth().height(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    val color = if (pagerState.currentPage == iteration) BrandGreenLight else DarkBorder
                    val width by animateDpAsState(
                        targetValue = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                        animationSpec = AnimSpec.spring(),
                        label = "width"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(width, 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            if (pagerState.currentPage < 2) {
                GlowButton(
                    text = "Continue",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                )
            } else {
                GlowButton(
                    text = "Get Started",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToLogin
                )
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Skip", style = TypographyTokens.Label, color = DarkTextSub)
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val (title, sub, color) = when (page) {
        0 -> Triple("Know Your Land", "किसान का भविष्य,\nDigital India", BrandGreenLight)
        1 -> Triple("AI in Your Pocket", "Instant disease detection and expert advice.", BrandSky)
        else -> Triple("Market Pulse", "Live prices from 1000+ Mandis.", BrandAmber)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for Illustration with Glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .glowShadow(color = color.copy(alpha = 0.3f), blurRadius = 40.dp)
                .clip(ShapeXL)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            // Inner decorative circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape)
            )
        }

        Spacer(Modifier.height(Spacing.huge))

        Text(
            text = title,
            style = TypographyTokens.DisplayL,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(Spacing.md))

        Text(
            text = sub,
            style = TypographyTokens.BodyL,
            color = DarkTextSub,
            textAlign = TextAlign.Center
        )
    }
}
