package com.shoping.agrismart.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.auth.AuthViewModel
import com.shoping.agrismart.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
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
                    .border(2.dp, BrandGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = DarkTextSub)
            }
            
            Spacer(Modifier.height(Spacing.m))
            
            Text(currentUser?.name ?: "Farmer", style = TypographyTokens.HeadingM, color = Color.White)
            Text(currentUser?.location ?: "Location not set", style = TypographyTokens.BodyM, color = DarkTextSub)
            
            Spacer(Modifier.height(Spacing.xl))
            
            ProfileItem("Email", currentUser?.email ?: "N/A")
            ProfileItem("Farm Size", currentUser?.farmSize ?: "N/A")
            ProfileItem("Primary Crop", currentUser?.primaryCrop ?: "N/A")
            
            Spacer(Modifier.weight(1f))
            
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                shape = ShapeM
            ) {
                Text("Logout", color = Color.Red)
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.s),
        color = DarkSurface,
        shape = ShapeM,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.m),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = DarkTextSub)
            Text(value, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}
