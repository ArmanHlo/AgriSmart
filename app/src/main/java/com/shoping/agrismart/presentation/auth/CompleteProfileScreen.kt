package com.shoping.agrismart.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.theme.*

@Composable
fun CompleteProfileScreen(
    onComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var primaryCrop by remember { mutableStateOf("") }

    LaunchedEffect(state.isProfileSaved) {
        if (state.isProfileSaved) {
            onComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Complete Your Profile",
                style = TypographyTokens.DisplayM,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                "Tell us about your farm for better advice",
                style = TypographyTokens.BodyM,
                color = DarkTextSub,
                modifier = Modifier.padding(top = Spacing.s)
            )

            Spacer(Modifier.height(Spacing.xxl))

            KrishiCard(
                modifier = Modifier.fillMaxWidth(),
                gradient = DarkSurface.toBrush()
            ) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    if (state.error != null) {
                        Text(state.error!!, color = Color.Red, style = TypographyTokens.Micro)
                        Spacer(Modifier.height(Spacing.s))
                    }

                    ProfileTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        label = "Full Name", 
                        placeholder = "Enter your name",
                        icon = Icons.Default.Person, 
                        enabled = !state.isLoading
                    )
                    Spacer(Modifier.height(Spacing.m))
                    ProfileTextField(
                        value = location, 
                        onValueChange = { location = it }, 
                        label = "Location", 
                        placeholder = "e.g. Nagpur, Maharashtra",
                        icon = Icons.Default.LocationOn, 
                        enabled = !state.isLoading
                    )
                    Spacer(Modifier.height(Spacing.m))
                    ProfileTextField(
                        value = farmSize, 
                        onValueChange = { farmSize = it }, 
                        label = "Farm Size (Acres)", 
                        placeholder = "e.g. 5",
                        icon = Icons.Default.Landscape, 
                        enabled = !state.isLoading
                    )
                    Spacer(Modifier.height(Spacing.m))
                    ProfileTextField(
                        value = primaryCrop, 
                        onValueChange = { primaryCrop = it }, 
                        label = "Primary Crop", 
                        placeholder = "e.g. Wheat",
                        icon = Icons.Default.Agriculture, 
                        enabled = !state.isLoading
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            GlowButton(
                text = if (state.isLoading) "Saving..." else "Save & Start Farming",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && name.isNotBlank() && location.isNotBlank(),
                onClick = {
                    viewModel.saveProfile(name, location, farmSize, primaryCrop)
                }
            )
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true
) {
    Column {
        Text(label.uppercase(), style = TypographyTokens.Label, color = DarkTextSub)
        Spacer(Modifier.height(Spacing.s))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = DarkTextSub.copy(alpha = 0.4f)) },
            leadingIcon = { Icon(icon, null, tint = BrandGreenGlow) },
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeM,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface2,
                unfocusedContainerColor = DarkSurface2,
                focusedBorderColor = BrandGreenLight,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )
    }
}
