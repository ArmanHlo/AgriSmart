package com.shoping.agrismart.presentation.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shoping.agrismart.presentation.auth.AuthViewModel
import com.shoping.agrismart.presentation.theme.*

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.res.stringResource
import com.shoping.agrismart.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current // Listen to config changes
    
    val currentLanguage = configuration.locales[0].language
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfileImage(it) }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkSurface2)
                    .border(2.dp, BrandGreen, CircleShape)
                    .clickable { if (!state.isLoading) imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (!currentUser?.profileImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = currentUser?.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = DarkTextSub)
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen, modifier = Modifier.size(32.dp))
                    }
                }
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            Text(currentUser?.name ?: stringResource(R.string.na), style = TypographyTokens.HeadingM, color = Color.White)
            Text(currentUser?.location ?: stringResource(R.string.not_set), style = TypographyTokens.BodyM, color = DarkTextSub)
            
            Spacer(Modifier.height(Spacing.xl))
            
            ProfileItem(stringResource(R.string.email), currentUser?.email ?: stringResource(R.string.na))
            
            val farmSizeDisplay = if (!currentUser?.farmSize.isNullOrBlank()) {
                "${currentUser?.farmSize} ${stringResource(R.string.acre)}"
            } else stringResource(R.string.na)
            ProfileItem(stringResource(R.string.farm_size), farmSizeDisplay)
            
            ProfileItem(stringResource(R.string.primary_crop), currentUser?.primaryCrop ?: stringResource(R.string.na))

            Spacer(Modifier.height(Spacing.m))

            // Language Selection
            Text(
                stringResource(R.string.app_language),
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.s),
                style = TypographyTokens.Label,
                color = DarkTextSub
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.s),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                PremiumChip(
                    label = "English",
                    selected = currentLanguage == "en",
                    onToggle = { 
                        Toast.makeText(context, context.getString(R.string.switching_to_english), Toast.LENGTH_SHORT).show()
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    },
                    modifier = Modifier.weight(1f)
                )
                PremiumChip(
                    label = "हिन्दी",
                    selected = currentLanguage == "hi",
                    onToggle = { 
                        Toast.makeText(context, context.getString(R.string.switching_to_hindi), Toast.LENGTH_SHORT).show()
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("hi")
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.weight(1f))
            
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                shape = ShapeM
            ) {
                Text(stringResource(R.string.logout), color = Color.Red)
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.s),
        color = DarkSurface,
        shape = ShapeM,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.m),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label",
                color = DarkTextSub,
                style = TypographyTokens.BodyM,
                modifier = Modifier.width(100.dp) // Fixed width for labels ensures alignment without huge gaps
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = TypographyTokens.BodyM,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
