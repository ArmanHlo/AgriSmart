package com.shoping.agrismart.presentation.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.domain.model.ChatMessage
import com.shoping.agrismart.domain.model.MessageRole
import com.shoping.agrismart.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.res.stringResource
import com.shoping.agrismart.R

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var inputText = remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // TTS Engine
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // STT Launcher
    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            spokenText?.let {
                inputText.value = it
                viewModel.onSendMessage(it)
                inputText.value = ""
            }
        }
    }

    // Voice Out logic: Speak when bot message arrives
    LaunchedEffect(state.messages.size) {
        val lastMessage = state.messages.lastOrNull()
        if (lastMessage != null && lastMessage.role == MessageRole.BOT) {
            tts?.speak(lastMessage.content, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // Auto-scroll to bottom...
    LaunchedEffect(state.messages.size, state.isLoading, state.error) {
        if (state.messages.isNotEmpty() || state.isLoading || state.error != null) {
            val lastIndex = if (state.error != null) state.messages.size else if (state.isLoading) state.messages.size else state.messages.size - 1
            if (lastIndex >= 0) {
                listState.animateScrollToItem(lastIndex)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatHeader(onBack)
            
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(top = Spacing.md, bottom = 120.dp)
                ) {
                    items(state.messages) { message ->
                        ChatBubble(message)
                    }
                    if (state.isLoading) {
                        item { TypingIndicator() }
                    }
                    state.error?.let { error ->
                        item {
                            ErrorBubble(error)
                        }
                    }
                }
            }
        }

        // Bottom Input Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(Spacing.md)
        ) {
            SuggestionChips { inputText.value = it }
            Spacer(Modifier.height(Spacing.s))
            ChatInputBar(
                text = inputText.value,
                onTextChange = { inputText.value = it },
                onSend = {
                    if (inputText.value.isNotBlank()) {
                        viewModel.onSendMessage(inputText.value)
                        inputText.value = ""
                    }
                },
                onVoiceClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }
                    sttLauncher.launch(intent)
                },
                isLoading = state.isLoading
            )
        }
    }
}

@Composable
fun ChatHeader(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface.copy(alpha = 0.8f),
        border = BorderStroke(0.5.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(Spacing.m))
            
            // Bot Avatar
            Box(modifier = Modifier.size(40.dp).background(BrandGreen.copy(0.2f), CircleShape)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.align(Alignment.Center), tint = BrandGreenGlow)
            }
            
            Spacer(Modifier.width(Spacing.m))
            
            Column {
                Text(stringResource(R.string.krishibot), style = TypographyTokens.HeadingM)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(SuccessGreen, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.online_ai_powered), style = TypographyTokens.Micro, color = DarkTextSub)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .glowShadow(
                        color = if (isUser) BrandGreen.copy(0.1f) else Color.Black.copy(0.1f),
                        blurRadius = 8.dp
                    ),
                color = if (isUser) BrandGreen else DarkSurface2,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                border = if (isUser) null else BorderStroke(1.dp, DarkBorder)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(14.dp),
                    style = TypographyTokens.BodyL,
                    color = Color.White
                )
            }
            Text(
                timeFormat.format(message.timestamp),
                style = TypographyTokens.Micro,
                color = DarkTextSub,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun ErrorBubble(error: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = DangerRed.copy(alpha = 0.1f),
            shape = ShapeM,
            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.2f)),
            modifier = Modifier.padding(Spacing.md)
        ) {
            Text(
                text = "Error: $error\nCheck your API key in local.properties",
                modifier = Modifier.padding(Spacing.m),
                style = TypographyTokens.BodyS,
                color = DangerRed
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .padding(Spacing.s)
            .clip(ShapePill)
            .background(DarkSurface2)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BrandGreenGlow.copy(alpha = alpha)))
        }
    }
}

@Composable
fun SuggestionChips(onSelect: (String) -> Unit) {
    val chips = listOf(
        stringResource(R.string.chip_wheat_prices),
        stringResource(R.string.chip_tomato_disease),
        stringResource(R.string.chip_pesticide_help),
        stringResource(R.string.chip_next_season)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(chips) { chip ->
            Surface(
                modifier = Modifier.clickable { onSelect(chip) },
                shape = ShapePill,
                color = DarkBg,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Text(
                    chip,
                    style = TypographyTokens.Micro,
                    color = BrandGreenGlow,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClick: () -> Unit,
    isLoading: Boolean
) {
    KrishiCard(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        gradient = Brush.linearGradient(listOf(DarkSurface2, DarkSurface2)),
        glowColor = Color.Black.copy(0.2f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Add, null, tint = DarkTextSub)
            }
            
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.ask_anything), style = TypographyTokens.BodyM, color = DarkTextSub) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BrandGreenGlow
                ),
                textStyle = TypographyTokens.BodyM.copy(color = Color.White),
                enabled = !isLoading
            )

            AnimatedVisibility(
                visible = text.isNotBlank() && !isLoading,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                IconButton(
                    onClick = onSend,
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(BrandGreen)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            if (text.isBlank() && !isLoading) {
                IconButton(onClick = onVoiceClick) {
                    Icon(Icons.Default.Mic, null, tint = BrandGreenGlow)
                }
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(4.dp),
                    color = BrandGreenGlow,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
