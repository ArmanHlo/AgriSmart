package com.shoping.agrismartapp.presentation.helpline

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shoping.agrismartapp.presentation.theme.*

@Composable
fun HelplineScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val helplines = listOf(
        Helpline("Kisan Call Centre", "1800-180-1551", "National agriculture assistance"),
        Helpline("Agri-Clinic Helpline", "1800-425-1556", "Expert production advice"),
        Helpline("PM-Kisan Support", "155261", "Scheme & payment queries"),
        Helpline("Crop Insurance", "1800-180-1551", "PMFBY related queries"),
        Helpline("Veterinary Care", "1962", "Emergency medical for livestock")
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HelplineHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                // Emergency Hero Card
                KrishiCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradient = GradientAmber,
                    glowColor = BrandAmber.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.xl),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Help, null, modifier = Modifier.size(28.dp), tint = Color.White)
                        }
                        Spacer(Modifier.width(Spacing.m))
                        Column {
                            Text("24/7 Assistance", style = TypographyTokens.HeadingM, color = Color.White)
                            Text("Connect with experts instantly", style = TypographyTokens.BodyS, color = Color.White.copy(0.8f))
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.xl))
                
                SectionHeader(title = "Important Helplines", subtitle = "One-tap connection")

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.m),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(helplines) { helpline ->
                        PremiumHelplineCard(
                            helpline = helpline,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${helpline.number}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HelplineHeader(onBack: () -> Unit) {
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
        Text("Helpline", style = TypographyTokens.HeadingL)
    }
}

data class Helpline(val name: String, val number: String, val description: String)

@Composable
fun PremiumHelplineCard(helpline: Helpline, onCall: () -> Unit) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush(),
        onClick = onCall
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(helpline.name, style = TypographyTokens.HeadingS)
                Text(helpline.number, style = TypographyTokens.DataNum, color = BrandGreenGlow)
                Text(helpline.description, style = TypographyTokens.Micro, color = DarkTextSub)
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Call, null, tint = BrandGreenGlow)
            }
        }
    }
}
