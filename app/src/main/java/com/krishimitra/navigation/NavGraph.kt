package com.krishimitra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.krishimitra.presentation.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Starting with Home for now as per Phase 1
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        // Add other composables here as we build them
    }
}
