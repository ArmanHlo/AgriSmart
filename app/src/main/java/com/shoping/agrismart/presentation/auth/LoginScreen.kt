package com.shoping.agrismart.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isVerified) {
        if (state.isVerified) {
            // Check the user object returned from the login action specifically
            val isProfileComplete = !state.user?.name.isNullOrBlank()
            onLoginSuccess(isProfileComplete)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(BrandGreen.copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1500f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.mega))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .glowShadow(color = BrandGreenGlow.copy(alpha = 0.3f), blurRadius = 30.dp)
                    .clip(ShapeXL)
                    .background(GradientGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("🌱", fontSize = 48.sp)
            }

            Spacer(Modifier.height(Spacing.xxl))

            KrishiCard(
                modifier = Modifier.fillMaxWidth(),
                gradient = DarkSurface.toBrush()
            ) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = Color.Red,
                            style = TypographyTokens.Micro,
                            modifier = Modifier.padding(bottom = Spacing.s)
                        )
                    }

                    Column {
                        Text("EMAIL", style = TypographyTokens.Label, color = DarkTextSub)
                        Spacer(Modifier.height(Spacing.s))
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            placeholder = { Text("farmer@example.com", color = DarkTextSub.copy(0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = BrandGreenGlow) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = ShapeM,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = BrandGreenLight,
                                unfocusedBorderColor = DarkBorder.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            enabled = !state.isLoading
                        )
                        Spacer(Modifier.height(Spacing.m))
                        Text("PASSWORD", style = TypographyTokens.Label, color = DarkTextSub)
                        Spacer(Modifier.height(Spacing.s))
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = BrandGreenGlow) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = ShapeM,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = BrandGreenLight,
                                unfocusedBorderColor = DarkBorder.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            enabled = !state.isLoading
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            GlowButton(
                text = if (state.isLoading) "Processing..." else "Sign In",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.loginWithEmail()
                },
                enabled = !state.isLoading
            )

            Spacer(Modifier.height(Spacing.m))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Create one", color = BrandGreenGlow)
            }

            Spacer(Modifier.weight(1f))

            Text(
                "By logging in, you agree to KrishiMitra's\nTerms of Service & Privacy Policy",
                style = TypographyTokens.Micro,
                color = DarkTextSub.copy(0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.xl)
            )
        }
    }
}
