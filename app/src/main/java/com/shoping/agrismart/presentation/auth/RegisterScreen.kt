package com.shoping.agrismart.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onRegisterSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    listOf(BrandGreen.copy(alpha = 0.15f), Color.Transparent),
                    radius = 1500f
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.mega))

            Box(
                modifier = Modifier.size(80.dp).clip(ShapeXL).background(GradientGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 40.sp)
            }

            Spacer(Modifier.height(Spacing.xl))

            Text("Create Account", style = TypographyTokens.DisplayM, color = Color.White)
            Text("Join our farming community", style = TypographyTokens.BodyM, color = DarkTextSub)

            Spacer(Modifier.height(Spacing.huge))

            KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface.toBrush()) {
                Column(modifier = Modifier.padding(Spacing.xl)) {
                    if (state.error != null) {
                        Text(state.error!!, color = Color.Red, style = TypographyTokens.Micro)
                        Spacer(Modifier.height(Spacing.s))
                    }

                    Text("EMAIL ADDRESS", style = TypographyTokens.Label, color = DarkTextSub)
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = BrandGreenGlow) },
                        modifier = Modifier.fillMaxWidth(),
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
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = BrandGreenGlow) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
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

                    Text("CONFIRM PASSWORD", style = TypographyTokens.Label, color = DarkTextSub)
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = BrandGreenGlow) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
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

            Spacer(Modifier.height(Spacing.xl))

            GlowButton(
                text = if (state.isLoading) "Creating Account..." else "Sign Up",
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.register() },
                enabled = !state.isLoading
            )

            Spacer(Modifier.height(Spacing.m))

            TextButton(onClick = onNavigateToLogin, enabled = !state.isLoading) {
                Text("Already have an account? Login", color = BrandGreenGlow)
            }
        }
    }
}
