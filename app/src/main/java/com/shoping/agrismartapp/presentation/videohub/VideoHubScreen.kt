package com.shoping.agrismartapp.presentation.videohub

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shoping.agrismartapp.domain.model.Video
import com.shoping.agrismartapp.presentation.theme.*

@Composable
fun VideoHubScreen(
    onBack: () -> Unit,
    viewModel: VideoHubViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val categories = listOf("All", "Crops", "Organic", "Irrigation", "Pests", "Marketing")

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            VideoHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                // Category Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.m),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    items(categories) { category ->
                        PremiumChip(
                            label = category,
                            selected = state.selectedCategory == category,
                            onToggle = { viewModel.onCategorySelected(category) }
                        )
                    }
                }

                if (state.isLoading) {
                    repeat(3) {
                        ShimmerBox(width = 400.dp, height = 240.dp, modifier = Modifier.padding(bottom = Spacing.m))
                    }
                } else if (state.error != null) {
                    EmptyStateView(
                        lottieRes = 0, // Placeholder
                        title = "Oops!",
                        subtitle = state.error!!,
                        actionText = "Retry",
                        onAction = { /* Retry logic */ }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.l),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(state.videos) { video ->
                            PremiumVideoCard(video)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoHeader(onBack: () -> Unit) {
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
        Text("Krishi Video Hub", style = TypographyTokens.HeadingL)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { }, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
            Icon(Icons.Default.Search, null, tint = BrandGreenGlow)
        }
    }
}

@Composable
fun PremiumVideoCard(video: Video) {
    val context = LocalContext.current
    
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush(),
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.videoUrl))
            context.startActivity(intent)
        }
    ) {
        Column {
            // Thumbnail with Overlay
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(ShapeTop),
                    contentScale = ContentScale.Crop
                )
                
                // Play Icon Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.withAlpha(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.PlayCircleFilled,
                        null,
                        modifier = Modifier.size(64.dp).glowShadow(color = BrandGreenGlow.withAlpha(0.4f)),
                        tint = Color.White
                    )
                }
                
                // Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Spacing.m)
                        .clip(ShapeS)
                        .background(Color.Black.withAlpha(0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(video.duration, style = TypographyTokens.Micro, color = Color.White)
                }
            }
            
            Column(modifier = Modifier.padding(Spacing.md)) {
                Text(video.title, style = TypographyTokens.HeadingS, maxLines = 2)
                Spacer(Modifier.height(Spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(text = video.category, type = StatusType.INFO)
                    Spacer(Modifier.width(Spacing.m))
                    Text("1.2k views", style = TypographyTokens.Micro, color = DarkTextSub)
                }
            }
        }
    }
}

// Helper for Color alpha
fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)
