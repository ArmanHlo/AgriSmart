package com.shoping.agrismart.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoping.agrismart.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNotesScreen(
    onBack: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Farming Notes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No saved notes yet. Ask KrishiBot and click pin to save!", color = DarkTextSub)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(notes) { note ->
                    KrishiCard(
                        modifier = Modifier.fillMaxWidth(),
                        gradient = DarkSurface2.toBrush()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(note.title, style = TypographyTokens.HeadingS, color = BrandGreenGlow)
                                IconButton(onClick = { viewModel.deleteNote(note) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(note.content, style = TypographyTokens.BodyM, color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(note.timestamp),
                                style = TypographyTokens.Micro,
                                color = DarkTextSub
                            )
                        }
                    }
                }
            }
        }
    }
}
