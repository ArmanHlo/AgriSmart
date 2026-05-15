package com.shoping.agrismart.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shoping.agrismart.presentation.auth.CompleteProfileScreen
import com.shoping.agrismart.presentation.auth.LoginScreen
import com.shoping.agrismart.presentation.auth.RegisterScreen
import com.shoping.agrismart.presentation.calendar.FarmingCalendarScreen
import com.shoping.agrismart.presentation.chat.ChatScreen
import com.shoping.agrismart.presentation.community.CommunityScreen
import com.shoping.agrismart.presentation.crop.CropRecommendationScreen
import com.shoping.agrismart.presentation.disease.DiseaseScannerScreen
import com.shoping.agrismart.presentation.home.HomeScreen
import com.shoping.agrismart.presentation.irrigation.IrrigationScreen
import com.shoping.agrismart.presentation.journal.FarmJournalScreen
import com.shoping.agrismart.presentation.market.MarketPriceScreen
import com.shoping.agrismart.presentation.notes.MyNotesScreen
import com.shoping.agrismart.presentation.onboarding.OnboardingScreen
import com.shoping.agrismart.presentation.pest.PestCalendarScreen
import com.shoping.agrismart.presentation.profile.ProfileScreen
import com.shoping.agrismart.presentation.schemes.SchemeScreen
import com.shoping.agrismart.presentation.soil.SoilHealthScreen
import com.shoping.agrismart.presentation.videohub.VideoHubScreen
import com.shoping.agrismart.presentation.weather.WeatherScreen

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
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.KrishiBot.route) {
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
                onBack = { navController.popBackStack() }
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
