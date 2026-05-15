package com.shoping.agrismartapp.presentation.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoping.agrismartapp.R

/**
 * 🎨 KRISHIMITRA LUXURY DESIGN SYSTEM — DESIGN TOKENS
 */

// BRAND CORE
val BrandGreen       = Color(0xFF1B5E20)
val BrandGreenLight  = Color(0xFF43A047)
val BrandGreenGlow   = Color(0xFF69F0AE)
val BrandAmber       = Color(0xFFF59E0B)
val BrandAmberLight  = Color(0xFFFCD34D)
val BrandSky         = Color(0xFF0EA5E9)
val BrandEarth       = Color(0xFF92400E)

// DARK THEME
val DarkBg           = Color(0xFF0A0F0A)
val DarkSurface      = Color(0xFF111811)
val DarkSurface2     = Color(0xFF1A231A)
val DarkBorder       = Color(0xFF2A3D2A)
val DarkText         = Color(0xFFF0FAF0)
val DarkTextSub      = Color(0xFF8BA98B)
val DarkGlass        = Color(0x1A43A047)

// LIGHT THEME
val LightBg          = Color(0xFFF0FAF0)
val LightSurface     = Color(0xFFFFFFFF)
val LightSurface2    = Color(0xFFE8F5E9)
val LightBorder      = Color(0xFFB2DFDB)
val LightText        = Color(0xFF0D1F0D)
val LightTextSub     = Color(0xFF4CAF50)

// SEMANTIC
val SuccessGreen     = Color(0xFF00C853)
val WarningAmber     = Color(0xFFFFAB00)
val DangerRed        = Color(0xFFFF1744)
val InfoBlue         = Color(0xFF00B0FF)
val RainBlue         = Color(0xFF1565C0)

// GRADIENTS
val GradientGreen    = Brush.linearGradient(listOf(Color(0xFF1B5E20), Color(0xFF43A047)))
val GradientAmber    = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
val GradientSky      = Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF6366F1)))
val GradientEarth    = Brush.linearGradient(listOf(Color(0xFF92400E), Color(0xFFF59E0B)))
val GradientNight    = Brush.linearGradient(listOf(Color(0xFF0A0F0A), Color(0xFF1A2E1A)))
val GradientMesh     = Brush.linearGradient(listOf(Color(0xFF0A1628), Color(0xFF0D2818), Color(0xFF1A0A28)))

// TYPOGRAPHY
val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs

)

val PlusJakartaSans = FontFamily(Font(googleFont = GoogleFont("Plus Jakarta Sans"), fontProvider = fontProvider))
val DMSans = FontFamily(Font(googleFont = GoogleFont("DM Sans"), fontProvider = fontProvider))
val JetBrainsMono = FontFamily(Font(googleFont = GoogleFont("JetBrains Mono"), fontProvider = fontProvider))
val Syne = FontFamily(Font(googleFont = GoogleFont("Syne"), fontProvider = fontProvider))

object TypographyTokens {
    val DisplayXL = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 56.sp)
    val DisplayL  = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 40.sp)
    val DisplayM  = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 32.sp)
    val HeadingL  = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
    val HeadingM  = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
    val HeadingS  = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    val BodyL     = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 16.sp)
    val BodyM     = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    val BodyS     = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 12.sp)
    val Label     = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.08.sp)
    val Price     = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 28.sp)
    val DataNum   = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
    val Micro     = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal, fontSize = 10.sp)
}

// SHAPES
val ShapeXS    = RoundedCornerShape(6.dp)
val ShapeS     = RoundedCornerShape(12.dp)
val ShapeM     = RoundedCornerShape(16.dp)
val ShapeL     = RoundedCornerShape(20.dp)
val ShapeXL    = RoundedCornerShape(28.dp)
val ShapePill  = RoundedCornerShape(50)
val ShapeTop   = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

// SPACING
object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val md = 16.dp
    val l = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val huge = 56.dp
    val mega = 80.dp
}

// ANIMATION
object AnimSpec {
    fun <T> spring(): SpringSpec<T> = androidx.compose.animation.core.spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    fun <T> springSnappy(): SpringSpec<T> = androidx.compose.animation.core.spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )
    fun <T> springSmooth(): SpringSpec<T> = androidx.compose.animation.core.spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val fast = tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)
    val medium = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
    val slow = tween<Float>(durationMillis = 500, easing = FastOutSlowInEasing)
    val lazy = tween<Float>(durationMillis = 800, easing = EaseOutCubic)
}

// MODIFIERS
fun Modifier.glowShadow(
    color: Color = Color(0x4043A047),
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 24.dp,
    offsetY: Dp = 8.dp
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            this.color = Color.Transparent.toArgb()
            setShadowLayer(blurRadius.toPx(), 0f, offsetY.toPx(), color.toArgb())
        }
        canvas.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, borderRadius.toPx(), borderRadius.toPx(), paint)
    }
}

fun Modifier.glassMorphism(blur: Dp = 20.dp, tint: Color = Color(0x1AFFFFFF), borderColor: Color = Color(0x26FFFFFF)): Modifier = this
    .background(tint)
    .border(1.dp, borderColor, ShapeM)

fun Color.asBrush() = Brush.linearGradient(listOf(this, this))
fun Color.toBrush() = Brush.linearGradient(listOf(this, this))
