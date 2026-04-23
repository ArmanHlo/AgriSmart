package com.krishimitra.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
    object CropAdvisor : Screen("crop_advisor")
    object DiseaseScanner : Screen("disease_scanner")
    object KrishiBot : Screen("krishi_bot")
    object Weather : Screen("weather")
    object MarketPrices : Screen("market_prices")
    object SoilHealth : Screen("soil_health")
    object GovtSchemes : Screen("govt_schemes")
    object FarmJournal : Screen("farm_journal")
    object Profile : Screen("profile")
}
