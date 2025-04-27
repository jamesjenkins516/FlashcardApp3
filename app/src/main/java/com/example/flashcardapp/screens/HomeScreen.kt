package com.example.flashcardapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.data.FlashcardDao
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    flashcardDao: FlashcardDao
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sets", "Create")

    // collect distinct set-names from your DAO
    val setNames by flashcardDao
        .getAllSetNames()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Flashcard App") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // —————— LIST OF SETS ——————
                    if (setNames.isEmpty()) {
                        Text("No sets yet. Switch to Create to add one.")
                    } else {
                        LazyColumn {
                            items(setNames) { setName ->
                                Text(
                                    text = setName,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            // encode spaces/special chars
                                            val encoded = URLEncoder.encode(setName, StandardCharsets.UTF_8.toString())
                                            navController.navigate("setDetail/$encoded")
                                        }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // —————— INLINE CREATE FORM ——————
                    CreateSetScreenContent(
                        flashcardDao = flashcardDao,
                        onDone = { selectedTab = 0 }
                    )
                }
            }
        }
    }
}
