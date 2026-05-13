package com.shoping.agrismart.presentation.disease

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shoping.agrismart.presentation.theme.*
import java.nio.ByteBuffer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.MediaStore

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiseaseScannerScreen(
    onBack: () -> Unit,
    viewModel: DiseaseScannerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = if (android.os.Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, it)
                android.graphics.ImageDecoder.decodeBitmap(source)
            }
            viewModel.onImageCaptured(bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true))
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (cameraPermissionState.status.isGranted) {
            // Immersive Camera Preview
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            
            // Scan Guide Overlay
            ScanOverlay(isLoading = state.isLoading)

            // Top Bar (Glassmorphism)
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(Spacing.md)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(DarkGlass)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                
                GlassCard(modifier = Modifier.weight(1f).padding(horizontal = Spacing.m), tintColor = DarkGlass) {
                    Text(
                        "Point at a leaf or crop",
                        style = TypographyTokens.BodyS,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    onClick = { /* Flash */ },
                    modifier = Modifier.clip(CircleShape).background(DarkGlass)
                ) {
                    Icon(Icons.Rounded.FlashOn, null, tint = Color.White)
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Capture Button
                CaptureButton(
                    isLoading = state.isLoading,
                    onClick = {
                        val executor = ContextCompat.getMainExecutor(context)
                        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                viewModel.onImageCaptured(image.toBitmap())
                                image.close()
                            }
                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Scanner", "Capture failed", exception)
                            }
                        })
                    }
                )
                
                Spacer(Modifier.height(Spacing.xl))
                
                Row(
                    modifier = Modifier
                        .alpha(0.7f)
                        .clip(ShapeM)
                        .clickable { galleryLauncher.launch("image/*") }
                        .padding(Spacing.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Import from Gallery", style = TypographyTokens.Label, color = Color.White)
                }
            }
        } else {
            EmptyStateView(
                lottieRes = 0, // Placeholder
                title = "Camera Access Required",
                subtitle = "We need camera permission to scan your crops for diseases.",
                actionText = "Grant Permission",
                onAction = { cameraPermissionState.launchPermissionRequest() }
            )
        }

        // Processing Overlay
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ProcessingOverlay()
        }

        // Result Bottom Sheet (Simulated as a card for now)
        AnimatedVisibility(
            visible = state.scanResult != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ResultView(state, onReset = viewModel::resetScanner)
        }
    }

    // Camera Init
    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
                } catch (e: Exception) { Log.e("CameraX", "Binding failed", e) }
            }, ContextCompat.getMainExecutor(context))
        }
    }
}

@Composable
fun ScanOverlay(isLoading: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "scanY"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Corner Brackets
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .border(2.dp, BrandGreenGlow.copy(alpha = 0.3f), ShapeXL)
        )
        
        // Scan Line
        if (!isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (200 + (scanY * 400)).dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, BrandGreenGlow, Color.Transparent)))
                    .glowShadow(color = BrandGreenGlow, blurRadius = 12.dp)
            )
        }
    }
}

@Composable
fun CaptureButton(isLoading: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = AnimSpec.springSnappy<Float>(),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .border(4.dp, Color.White.copy(0.3f), CircleShape)
            .padding(8.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(interactionSource = interactionSource, indication = null, enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = BrandGreen, modifier = Modifier.size(32.dp))
        } else {
            Icon(Icons.Default.Camera, null, tint = BrandGreen, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun ProcessingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBg.copy(0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // DNA Helix Loader (Simplified with Circular)
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = BrandGreenGlow,
                strokeWidth = 4.dp
            )
            Spacer(Modifier.height(Spacing.xl))
            Text("Analyzing Crop Pattern...", style = TypographyTokens.HeadingM, color = Color.White)
            Text("Checking for 38 disease markers", style = TypographyTokens.BodyS, color = DarkTextSub)
        }
    }
}

@Composable
fun ResultView(state: DiseaseScannerState, onReset: () -> Unit) {
    val result = state.scanResult ?: return
    val isHealthy = result.diseaseName.lowercase().contains("healthy")
    
    Box(
        modifier = Modifier.fillMaxSize().background(DarkBg.copy(0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.huge))
            
            // Result Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(if (isHealthy) SuccessGreen.copy(0.1f) else DangerRed.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                    null,
                    modifier = Modifier.size(80.dp),
                    tint = if (isHealthy) SuccessGreen else DangerRed
                )
            }
            
            Spacer(Modifier.height(Spacing.xl))
            
            Text(
                if (isHealthy) "Crop Looks Healthy!" else "Disease Detected",
                style = TypographyTokens.DisplayM,
                color = if (isHealthy) SuccessGreen else DangerRed,
                textAlign = TextAlign.Center
            )
            
            StatusPill(text = "${(result.confidence * 100).toInt()}% Confidence", type = if (isHealthy) StatusType.SUCCESS else StatusType.WARNING)
            
            Spacer(Modifier.height(Spacing.xl))
            
            KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface2.toBrush()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text("CROP DETECTED", style = TypographyTokens.Label, color = DarkTextSub)
                    Text(result.cropName, style = TypographyTokens.HeadingM, color = Color.White)
                    
                    if (!isHealthy) {
                        Spacer(Modifier.height(Spacing.md))
                        Text("CONDITION", style = TypographyTokens.Label, color = DarkTextSub)
                        Text(result.diseaseName, style = TypographyTokens.HeadingM, color = DangerRed)
                    }
                }
            }
            
            Spacer(Modifier.height(Spacing.md))
            
            KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface.toBrush()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(if (isHealthy) "CARE TIPS" else "TREATMENT PLAN", style = TypographyTokens.Label, color = DarkTextSub)
                    Text(result.treatment, style = TypographyTokens.BodyM, color = Color.White)
                }
            }

            Spacer(Modifier.height(Spacing.xxl))
            
            GlowButton(text = "Scan Another", modifier = Modifier.fillMaxWidth(), onClick = onReset)
            
            Spacer(Modifier.height(Spacing.m))
            
            TextButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
                Text("Save to Farm Journal", style = TypographyTokens.BodyM, color = BrandGreenGlow)
            }
            
            Spacer(Modifier.height(Spacing.huge))
        }
    }
}

private fun ImageProxy.toBitmap(): Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
