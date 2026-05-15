package com.shoping.agrismartapp.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shoping.agrismartapp.presentation.auth.CompleteProfileScreen
import com.shoping.agrismartapp.presentation.auth.LoginScreen
import com.shoping.agrismartapp.presentation.auth.RegisterScreen
import com.shoping.agrismartapp.presentation.calendar.FarmingCalendarScreen
import com.shoping.agrismartapp.presentation.chat.ChatScreen
import com.shoping.agrismartapp.presentation.community.CommunityScreen
import com.shoping.agrismartapp.presentation.crop.CropRecommendationScreen
import com.shoping.agrismartapp.presentation.disease.DiseaseScannerScreen
import com.shoping.agrismartapp.presentation.home.HomeScreen
import com.shoping.agrismartapp.presentation.irrigation.IrrigationScreen
import com.shoping.agrismartapp.presentation.journal.FarmJournalScreen
import com.shoping.agrismartapp.presentation.market.MarketPriceScreen
import com.shoping.agrismartapp.presentation.notes.MyNotesScreen
import com.shoping.agrismartapp.presentation.onboarding.OnboardingScreen
import com.shoping.agrismartapp.presentation.pest.PestCalendarScreen
import com.shoping.agrismartapp.presentation.profile.ProfileScreen
import com.shoping.agrismartapp.presentation.schemes.SchemeScreen
import com.shoping.agrismartapp.presentation.soil.SoilHealthScreen
import com.shoping.agrismartapp.presentation.videohub.VideoHubScreen
import com.shoping.agrismartapp.presentation.weather.WeatherScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500)) }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isProfileComplete -> 
                    if (isProfileComplete) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.CropAdvisor.route) {
            CropRecommendationScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(
            route = Screen.KrishiBot.route + "?prompt={prompt}",
            arguments = listOf(navArgument("prompt") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            ChatScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.DiseaseScanner.route) {
            DiseaseScannerScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Weather.route) {
            WeatherScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MarketPrices.route) {
            MarketPriceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FarmJournal.route) {
            FarmJournalScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.GovtSchemes.route) {
            SchemeScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Irrigation.route) {
            IrrigationScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Community.route) {
            CommunityScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.VideoHub.route) {
            VideoHubScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SoilHealth.route) {
            SoilHealthScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PestCalendar.route) {
            PestCalendarScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FarmingCalendar.route) {
            FarmingCalendarScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MyNotes.route) {
            MyNotesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
