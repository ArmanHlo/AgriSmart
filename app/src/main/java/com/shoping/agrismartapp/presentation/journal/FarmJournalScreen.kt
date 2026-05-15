package com.shoping.agrismartapp.presentation.journal

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismartapp.data.local.entity.FarmActivityEntity
import com.shoping.agrismartapp.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FarmJournalScreen(
    onBack: () -> Unit,
    viewModel: FarmJournalViewModel = hiltViewModel()
) {
    val activities by viewModel.activities.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)
    val totalIncome by viewModel.totalIncome.collectAsState(initial = 0.0)
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            JournalHeader(onBack)
            
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                CalendarStrip()
                
                Spacer(Modifier.height(Spacing.xl))
                
                JournalStatsSummary(
                    expenses = totalExpenses ?: 0.0,
                    income = totalIncome ?: 0.0
                )
                
                Spacer(Modifier.height(Spacing.xl))
                
                SectionHeader(title = "Activity Log", subtitle = "Your farming timeline")
                
                if (activities.isEmpty()) {
                    EmptyStateView(
                        lottieRes = 0, // Placeholder
                        title = "No Entries Yet",
                        subtitle = "Start logging your farm activities to track progress.",
                        actionText = "Add First Entry",
                        onAction = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.m),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(activities) { activity ->
                            TimelineEntry(activity, onDelete = { viewModel.deleteActivity(activity) })
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.xl)
                .glowShadow(color = BrandGreenGlow.copy(alpha = 0.4f)),
            containerColor = BrandGreen,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Entry")
        }
    }

    if (showAddDialog) {
        PremiumAddEntryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, crop, cost, income, notes ->
                viewModel.addActivity(
                    type = type,
                    cropName = crop,
                    date = System.currentTimeMillis(),
                    cost = cost,
                    income = income,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun JournalHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(Spacing.md)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        Spacer(Modifier.width(Spacing.m))
        Text("Farm Journal", style = TypographyTokens.HeadingL)
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = { /* Export PDF Mock */ }, 
            modifier = Modifier.clip(CircleShape).background(DarkSurface2)
        ) {
            Icon(Icons.Default.PictureAsPdf, null, tint = Color.White)
        }
        Spacer(Modifier.width(Spacing.s))
        IconButton(onClick = { /* Analytics */ }, modifier = Modifier.clip(CircleShape).background(DarkSurface2)) {
            Icon(Icons.Default.BarChart, null, tint = BrandGreenGlow)
        }
    }
}

@Composable
fun CalendarStrip() {
    val days = (0..14).map { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, it - 7) }.time }
    val today = Calendar.getInstance().time
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        contentPadding = PaddingValues(horizontal = Spacing.xs)
    ) {
        items(days) { date ->
            val isToday = SimpleDateFormat("dd", Locale.getDefault()).format(date) == 
                          SimpleDateFormat("dd", Locale.getDefault()).format(today)
            
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .clip(ShapeM)
                    .background(if (isToday) BrandGreen else DarkSurface2)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase(),
                    style = TypographyTokens.Micro,
                    color = if (isToday) Color.White else DarkTextSub
                )
                Text(
                    SimpleDateFormat("dd", Locale.getDefault()).format(date),
                    style = TypographyTokens.HeadingS,
                    color = if (isToday) Color.White else DarkText
                )
                if (isToday) {
                    Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                }
            }
        }
    }
}

@Composable
fun JournalStatsSummary(expenses: Double, income: Double) {
    val profit = income - expenses
    
    KrishiCard(modifier = Modifier.fillMaxWidth(), gradient = DarkSurface.toBrush()) {
        Row(
            modifier = Modifier.padding(Spacing.md).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            JournalStatItem("Expense", "₹${expenses.toInt()}", DangerRed)
            JournalStatItem("Income", "₹${income.toInt()}", SuccessGreen)
            JournalStatItem("Profit", "₹${profit.toInt()}", BrandGreenGlow)
        }
    }
}

@Composable
fun JournalStatItem(label: String, value: String, color: Color) {
    Column {
        Text(label.uppercase(), style = TypographyTokens.Micro, color = DarkTextSub)
        Text(value, style = TypographyTokens.DataNum, color = color)
    }
}

@Composable
fun TimelineEntry(activity: FarmActivityEntity, onDelete: () -> Unit) {
    val nodeColor = when (activity.type.lowercase()) {
        "irrigation" -> InfoBlue
        "sowing" -> SuccessGreen
        "pesticide" -> WarningAmber
        "sale" -> BrandAmber
        else -> BrandGreenGlow
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(nodeColor).glowShadow(color = nodeColor, blurRadius = 8.dp))
            Box(modifier = Modifier.width(2.dp).weight(1f).background(DarkBorder))
        }
        
        Spacer(Modifier.width(Spacing.m))
        
        KrishiCard(
            modifier = Modifier.weight(1f),
            gradient = DarkSurface2.toBrush()
        ) {
            Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(activity.type, style = TypographyTokens.HeadingS)
                        Spacer(Modifier.width(Spacing.s))
                        Text(
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(activity.date)),
                            style = TypographyTokens.Micro,
                            color = DarkTextSub
                        )
                    }
                    Text(activity.cropName, style = TypographyTokens.BodyS, color = DarkTextSub)
                    activity.notes?.let {
                        if (it.isNotBlank()) {
                            Text(it, style = TypographyTokens.Micro, color = DarkTextSub.copy(0.7f), maxLines = 1)
                        }
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    if (activity.income != null && activity.income!! > 0) {
                        Text("+₹${activity.income?.toInt()}", style = TypographyTokens.BodyL, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    } else if (activity.cost != null && activity.cost!! > 0) {
                        Text("-₹${activity.cost?.toInt()}", style = TypographyTokens.BodyL, color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = DarkBorder, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumAddEntryDialog(onDismiss: () -> Unit, onSave: (String, String, Double?, Double?, String) -> Unit) {
    var type by remember { mutableStateOf("Sowing") }
    var crop by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Log Farm Activity", style = TypographyTokens.HeadingM) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                val types = listOf("Sowing", "Irrigation", "Pesticide", "Harvest", "Sale")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
                    items(types) { t ->
                        PremiumChip(label = t, selected = type == t, onToggle = { type = t })
                    }
                }
                
                OutlinedTextField(
                    value = crop,
                    onValueChange = { crop = it },
                    label = { Text("Crop Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeM,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandGreenLight)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { if (it.all { c -> c.isDigit() }) cost = it },
                        label = { Text("Expense (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = ShapeM
                    )
                    OutlinedTextField(
                        value = income,
                        onValueChange = { if (it.all { c -> c.isDigit() }) income = it },
                        label = { Text("Income (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = ShapeM
                    )
                }
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeM,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            GlowButton(text = "Save Entry", onClick = { 
                onSave(type, crop, cost.toDoubleOrNull(), income.toDoubleOrNull(), notes) 
            })
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DarkTextSub) }
        }
    )
}
