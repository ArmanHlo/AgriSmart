package com.shoping.agrismart.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
    object CropAdvisor : Screen("crop_advisor")
    object DiseaseScanner : Screen("disease_scanner")
    object KrishiBot : Screen("krishi_bot")
    object Weather : Screen("weather")
    object MarketPrices : Screen("market_prices")
    object FarmJournal : Screen("farm_journal")
    object GovtSchemes : Screen("govt_schemes")
    object Irrigation : Screen("irrigation")
    object Community : Screen("community")
    object VideoHub : Screen("video_hub")
    object SoilHealth : Screen("soil_health")
    object PestCalendar : Screen("pest_calendar")
    object Helpline : Screen("helpline")
    object Profile : Screen("profile")
}
