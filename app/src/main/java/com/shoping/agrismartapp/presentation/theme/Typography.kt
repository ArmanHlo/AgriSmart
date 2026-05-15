package com.shoping.agrismartapp.presentation.theme

import androidx.compose.material3.Typography

val KrishiTypography = Typography(
    displayLarge = TypographyTokens.DisplayXL,
    displayMedium = TypographyTokens.DisplayL,
    displaySmall = TypographyTokens.DisplayM,
    headlineLarge = TypographyTokens.HeadingL,
    headlineMedium = TypographyTokens.HeadingM,
    headlineSmall = TypographyTokens.HeadingS,
    bodyLarge = TypographyTokens.BodyL,
    bodyMedium = TypographyTokens.BodyM,
    bodySmall = TypographyTokens.BodyS,
    labelLarge = TypographyTokens.Label
)

// Extension for convenience if needed elsewhere
object KrishiTextStyles {
    val Price = TypographyTokens.Price
    val DataNum = TypographyTokens.DataNum
    val Micro = TypographyTokens.Micro
}
