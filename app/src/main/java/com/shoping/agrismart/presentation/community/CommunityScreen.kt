package com.shoping.agrismart.presentation.community

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.domain.model.Comment
import com.shoping.agrismart.domain.model.Post
import com.shoping.agrismart.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onBack: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBg
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                CommunityHeader(onBack)
                
                Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                    // Topic Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        contentPadding = PaddingValues(vertical = Spacing.m)
                    ) {
                        val topics = listOf("All", "Wheat", "Rice", "Organic", "Pests", "Market", "Tools")
                        items(topics) { topic ->
                            PremiumChip(label = topic, selected = topic == "All") { }
                        }
                    }

                    if (state.isLoading && state.posts.isEmpty()) {
                        repeat(3) {
                            ShimmerBox(width = 400.dp, height = 200.dp, modifier = Modifier.padding(bottom = Spacing.m))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.m),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(state.posts) { post ->
                                PremiumPostCard(
                                    post = post,
                                    onLike = { viewModel.likePost(post.id) },
                                    onCommentClick = { viewModel.selectPostForComments(post.id) }
                                )
                            }
                        }
                    }
                }
            }

            // New Post FAB
            FloatingActionButton(
                onClick = { viewModel.togglePostSheet(true) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacing.xl)
                    .glowShadow(color = BrandGreenGlow.copy(alpha = 0.4f)),
                containerColor = BrandGreenLight,
                contentColor = Color.White,
                shape = ShapeXL
            ) {
                Icon(Icons.Default.AddComment, contentDescription = "New Post")
            }

            if (state.isPostSheetVisible) {
                PremiumCreatePostDialog(
                    content = state.postContent,
                    onContentChange = viewModel::onPostContentChange,
                    onDismiss = { viewModel.togglePostSheet(false) },
                    onPost = viewModel::createPost,
                    selectedTopic = state.selectedTopic,
                    onTopicChange = viewModel::onTopicChange
                )
            }

            if (state.selectedPostId != null) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.selectPostForComments(null) },
                    sheetState = sheetState,
                    containerColor = DarkSurface,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = DarkBorder) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        Text(
                            "Comments",
                            style = TypographyTokens.HeadingM,
                            modifier = Modifier.padding(Spacing.md),
                            color = Color.White
                        )
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .padding(horizontal = Spacing.md),
                            verticalArrangement = Arrangement.spacedBy(Spacing.m)
                        ) {
                            if (state.comments.isEmpty()) {
                                item {
                                    Text(
                                        "No comments yet. Be the first to reply!",
                                        style = TypographyTokens.BodyM,
                                        color = DarkTextSub,
                                        modifier = Modifier.padding(vertical = Spacing.xl).fillMaxWidth(),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                            items(state.comments) { comment ->
                                CommentItem(comment)
                            }
                        }
                        
                        Divider(color = DarkBorder, thickness = 0.5.dp)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = state.commentText,
                                onValueChange = { viewModel.onCommentChange(it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 52.dp),
                                placeholder = { Text("Write a comment...", color = DarkTextSub) },
                                shape = ShapePill,
                                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = DarkSurface2,
                                    unfocusedContainerColor = DarkSurface2,
                                    focusedBorderColor = BrandGreenLight,
                                    unfocusedBorderColor = DarkBorder,
                                    cursorColor = BrandGreenGlow
                                )
                            )
                            Spacer(Modifier.width(Spacing.s))
                            FilledIconButton(
                                onClick = viewModel::submitComment,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = BrandGreenLight,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, null)
                            }
                        }
                        Spacer(Modifier.height(Spacing.s))
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    val dateStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(comment.timestamp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeM)
            .background(DarkSurface2)
            .padding(Spacing.md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(comment.authorName.take(1), style = TypographyTokens.Micro, color = BrandGreenGlow)
            }
            Spacer(Modifier.width(Spacing.s))
            Text(comment.authorName, style = TypographyTokens.Label, color = Color.White)
            Spacer(Modifier.weight(1f))
            Text(dateStr, style = TypographyTokens.Micro, color = DarkTextSub)
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(comment.content, style = TypographyTokens.BodyS, color = DarkText)
    }
}

@Composable
fun CommunityHeader(onBack: () -> Unit) {
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
        Text("Kisan Community", style = TypographyTokens.HeadingL, color = Color.White)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { }, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
            Icon(Icons.Default.Search, null, tint = BrandGreenGlow)
        }
    }
}

@Composable
fun PremiumPostCard(post: Post, onLike: () -> Unit, onCommentClick: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(post.timestamp)
    
    KrishiCard(
        modifier = Modifier.fillMaxWidth(),
        gradient = DarkSurface.toBrush()
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Author Avatar with gradient ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.5.dp, BrandGreenLight, CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.authorName.take(1), style = TypographyTokens.HeadingS, color = BrandGreenGlow)
                }
                
                Spacer(Modifier.width(Spacing.md))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, style = TypographyTokens.BodyL, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(dateStr, style = TypographyTokens.Micro, color = DarkTextSub)
                }
                
                StatusPill(text = post.topic, type = StatusType.INFO)
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            Text(post.content, style = TypographyTokens.BodyM, color = DarkText)
            
            // Post Image Placeholder (Luxury Tier apps usually have images)
            val shouldShowImage = remember(post.id) {
                try {
                    post.id.hashCode() % 2 == 0
                } catch (e: Exception) {
                    false
                }
            }

            if (shouldShowImage) {
                Spacer(Modifier.height(Spacing.m))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(ShapeM)
                        .background(DarkSurface2)
                ) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center).alpha(0.1f), tint = Color.White)
                }
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            Divider(color = DarkBorder, thickness = 0.5.dp)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.s),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike) {
                        Icon(
                            if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (post.isLiked) DangerRed else DarkTextSub
                        )
                    }
                    Text("${post.likes}", style = TypographyTokens.BodyS, color = if (post.isLiked) DangerRed else DarkTextSub)
                    
                    Spacer(Modifier.width(Spacing.m))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCommentClick() }
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, null, tint = DarkTextSub)
                        Spacer(Modifier.width(Spacing.xs))
                        Text("${post.commentCount} Comments", style = TypographyTokens.BodyS, color = DarkTextSub)
                    }
                }
                
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Share, null, tint = DarkTextSub)
                }
            }
        }
    }
}

@Composable
fun PremiumCreatePostDialog(
    content: String,
    onContentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onPost: () -> Unit,
    selectedTopic: String,
    onTopicChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("New Post", style = TypographyTokens.HeadingM, color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Share your farming experience...", style = TypographyTokens.BodyM, color = DarkTextSub) },
                    shape = ShapeM,
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = BrandGreenLight,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = BrandGreenLight
                    )
                )
                
                Text("Select Topic", style = TypographyTokens.Label, color = DarkTextSub)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    val topics = listOf("General", "Wheat", "Rice", "Pests", "Market")
                    items(topics) { topic ->
                        PremiumChip(label = topic, selected = selectedTopic == topic) { onTopicChange(topic) }
                    }
                }
            }
        },
        confirmButton = {
            GlowButton(text = "Post Now", onClick = onPost)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DarkTextSub) }
        }
    )
}
