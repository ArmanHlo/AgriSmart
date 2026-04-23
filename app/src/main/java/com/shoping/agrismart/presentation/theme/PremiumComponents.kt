package com.shoping.agrismart.presentation.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

/**
 * 🛠 KRISHIMITRA PREMIUM REUSABLE COMPONENTS
 */

// 1. KrishiCard
@Composable
fun KrishiCard(
    modifier: Modifier = Modifier,
    gradient: Brush = GradientNight,
    glowColor: Color = Color(0x4043A047),
    contentAlignment: Alignment = Alignment.Center,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = AnimSpec.springSnappy(),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .glowShadow(color = glowColor)
            .clip(ShapeL)
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(1.dp) // Border effect
            .background(gradient, ShapeL),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

// 2. GlassCard
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 20.dp,
    tintColor: Color = DarkGlass,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .glassMorphism(blur = blurRadius, tint = tintColor),
        color = Color.Transparent,
        shape = ShapeL
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            content()
        }
    }
}

// 3. AnimatedCounter
@Composable
fun AnimatedCounter(
    target: Int,
    modifier: Modifier = Modifier,
    unit: String = "",
    style: androidx.compose.ui.text.TextStyle = TypographyTokens.DataNum,
    color: Color = Color.Unspecified
) {
    val animatedCount by animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "count"
    )

    Text(
        text = "$animatedCount$unit",
        modifier = modifier,
        style = style,
        color = color,
        fontFamily = JetBrainsMono
    )
}

// 4. GlowButton
@Composable
fun GlowButton(
    text: String,
    modifier: Modifier = Modifier,
    gradient: Brush = GradientGreen,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1f,
        animationSpec = AnimSpec.springSnappy(),
        label = "scale"
    )

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .glowShadow(color = if (enabled) BrandGreenGlow.copy(alpha = 0.5f) else Color.Transparent, borderRadius = 50.dp),
        contentPadding = PaddingValues(0.dp),
        shape = ShapePill,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        interactionSource = interactionSource,
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (enabled) gradient else SolidColor(DarkSurface2.copy(alpha = 0.8f)))
                .padding(horizontal = 24.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(text, style = TypographyTokens.HeadingS, color = if (enabled) Color.White else DarkTextSub)
            }
        }
    }
}

// 5. ShimmerBox
@Composable
fun ShimmerBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = ShapeS
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.1f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .size(width, height)
            .clip(shape)
            .background(brush)
    )
}

// 6. PremiumChip
@Composable
fun PremiumChip(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(if (selected) BrandGreen else DarkSurface2, label = "bgColor")
    val textColor by animateColorAsState(if (selected) Color.White else DarkTextSub, label = "textColor")
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = AnimSpec.spring(),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable { onToggle() },
        shape = ShapePill,
        color = bgColor,
        border = if (!selected) BorderStroke(1.dp, DarkBorder) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                Spacer(Modifier.width(6.dp))
            }
            Text(label, style = TypographyTokens.Label, color = textColor)
        }
    }
}

// 7. NeonProgressBar
@Composable
fun NeonProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    color: Color = BrandGreenGlow
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = AnimSpec.slow,
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(ShapePill)
            .background(DarkBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(ShapePill)
                .background(color)
                .glowShadow(color = color.copy(alpha = 0.4f), blurRadius = 8.dp)
        )
    }
}

// 8. StatusPill
enum class StatusType { SUCCESS, WARNING, ERROR, INFO }

@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    type: StatusType = StatusType.INFO
) {
    val (bgColor, contentColor) = when (type) {
        StatusType.SUCCESS -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        StatusType.WARNING -> WarningAmber.copy(alpha = 0.15f) to WarningAmber
        StatusType.ERROR   -> DangerRed.copy(alpha = 0.15f) to DangerRed
        StatusType.INFO    -> InfoBlue.copy(alpha = 0.15f) to InfoBlue
    }

    Row(
        modifier = modifier
            .clip(ShapePill)
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing Dot
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "alpha"
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = alpha))
        )
        Spacer(Modifier.width(6.dp))
        Text(text.uppercase(), style = TypographyTokens.Micro, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

// 9. WeatherMiniWidget
@Composable
fun WeatherMiniWidget(
    temp: String,
    icon: ImageVector = Icons.Default.Cloud,
    humidity: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.width(140.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = BrandSky, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(temp, style = TypographyTokens.HeadingM)
        }
        Text("Humidity: $humidity", style = TypographyTokens.Micro, color = DarkTextSub)
    }
}

// 10. CropScoreGauge
@Composable
fun CropScoreGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val animatedScore by animateFloatAsState(targetValue = score.toFloat(), animationSpec = AnimSpec.lazy, label = "score")
    
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = DarkBorder,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
        )
        CircularProgressIndicator(
            progress = { animatedScore / 100f },
            modifier = Modifier.fillMaxSize(),
            color = if (score > 70) SuccessGreen else if (score > 40) WarningAmber else DangerRed,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = score.toString(), style = TypographyTokens.DisplayM)
            Text(text = "/100", style = TypographyTokens.BodyS, color = DarkTextSub)
        }
    }
}

// 11. PriceTag
@Composable
fun PriceTag(
    price: String,
    change: String,
    isUp: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isUp) SuccessGreen else DangerRed
    val icon = if (isUp) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(price, style = TypographyTokens.DataNum, color = DarkText)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = color)
            Text(change, style = TypographyTokens.Micro, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

// 12. SectionHeader
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(title, style = TypographyTokens.HeadingL)
            if (subtitle != null) {
                Text(subtitle, style = TypographyTokens.BodyS, color = DarkTextSub)
            }
        }
        if (actionText != null) {
            Text(
                actionText,
                modifier = Modifier.clickable { onAction?.invoke() },
                style = TypographyTokens.Label,
                color = BrandGreenGlow
            )
        }
    }
}

// 13. EmptyStateView
@Composable
fun EmptyStateView(
    lottieRes: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp)
        )
        Spacer(Modifier.height(Spacing.l))
        Text(title, style = TypographyTokens.HeadingM, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.s))
        Text(subtitle, style = TypographyTokens.BodyM, color = DarkTextSub, textAlign = TextAlign.Center)
        if (actionText != null) {
            Spacer(Modifier.height(Spacing.xl))
            GlowButton(text = actionText, onClick = { onAction?.invoke() })
        }
    }
}

// 14. FullScreenLoader
@Composable
fun FullScreenLoader(message: String = "Loading...") {
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBg.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = BrandGreenGlow)
            Spacer(Modifier.height(Spacing.md))
            Text(message, style = TypographyTokens.BodyM, color = BrandGreenGlow)
        }
    }
}

// 15. SnackbarKrishi
@Composable
fun SnackbarKrishi(
    snackbarData: SnackbarData,
    type: StatusType = StatusType.INFO
) {
    val (bgColor, contentColor) = when (type) {
        StatusType.SUCCESS -> SuccessGreen.copy(alpha = 0.9f) to Color.White
        StatusType.WARNING -> WarningAmber.copy(alpha = 0.9f) to Color.Black
        StatusType.ERROR   -> DangerRed.copy(alpha = 0.9f) to Color.White
        StatusType.INFO    -> DarkSurface2.copy(alpha = 0.9f) to DarkText
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .glowShadow(color = bgColor.copy(alpha = 0.3f)),
        shape = ShapeM,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snackbarData.visuals.message,
                style = TypographyTokens.BodyM,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            snackbarData.visuals.actionLabel?.let { action ->
                TextButton(onClick = { snackbarData.performAction() }) {
                    Text(action, color = contentColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
