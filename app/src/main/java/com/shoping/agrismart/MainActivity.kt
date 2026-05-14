package com.shoping.agrismart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shoping.agrismart.presentation.auth.AuthViewModel
import com.shoping.agrismart.presentation.navigation.NavGraph
import com.shoping.agrismart.presentation.navigation.Screen
import com.shoping.agrismart.presentation.theme.DarkBg
import com.shoping.agrismart.presentation.theme.KrishiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrishiTheme {
                val navController = rememberNavController()
                val isAuthLoading by authViewModel.isAuthLoading.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                // We compute the destination based on the current user state
                // If the user is logged in but the name is missing, we check if we're still loading Firestore data
                val startDestination = remember(currentUser, isAuthLoading) {
                    when {
                        isAuthLoading -> null // Still determining
                        currentUser == null -> Screen.Onboarding.route
                        !currentUser?.name.isNullOrBlank() -> Screen.Home.route
                        else -> Screen.CompleteProfile.route
                    }
                }

                if (startDestination == null) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    LaunchedEffect(currentUser) {
                        if (currentUser == null) {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}
