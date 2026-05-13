package com.krishimitra.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.krishimitra.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KrishiMitra", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            GreetingCard(farmerName = "Farmer")
            Spacer(modifier = Modifier.height(16.dp))
            WeatherWidget()
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsGrid(navController)
        }
    }
}

@Composable
fun GreetingCard(farmerName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Jai Kisan, $farmerName!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "Today is ${java.time.LocalDate.now()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WeatherWidget() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "New Delhi", style = MaterialTheme.typography.titleMedium)
                Text(text = "32°C", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(text = "Sunny", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavHostController) {
    val actions = listOf(
        ActionItem("Crop Advisor", Icons.Default.Agriculture, Screen.CropAdvisor.route),
        ActionItem("Disease Scan", Icons.Default.CameraAlt, Screen.DiseaseScanner.route),
        ActionItem("KrishiBot", Icons.Default.Chat, Screen.KrishiBot.route),
        ActionItem("Market Price", Icons.Default.TrendingUp, Screen.MarketPrices.route),
        ActionItem("Weather", Icons.Default.Cloud, Screen.Weather.route),
        ActionItem("Soil Health", Icons.Default.Science, Screen.SoilHealth.route),
        ActionItem("Govt Schemes", Icons.Default.AccountBalance, Screen.GovtSchemes.route),
        ActionItem("My Farm", Icons.Default.Book, Screen.FarmJournal.route)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(actions) { action ->
            ActionCard(action) {
                navController.navigate(action.route)
            }
        }
    }
}

@Composable
fun ActionCard(action: ActionItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = action.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = action.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Explore") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Camera, contentDescription = "Scan") },
            label = { Text("Scan") },
            selected = false,
            onClick = { navController.navigate(Screen.DiseaseScanner.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { }
        )
    }
}

data class ActionItem(val title: String, val icon: ImageVector, val route: String)
