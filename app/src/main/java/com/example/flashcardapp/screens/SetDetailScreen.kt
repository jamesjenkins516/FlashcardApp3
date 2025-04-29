package com.example.flashcardapp.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.provider.CalendarContract
import java.util.Calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDetailScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    setName: String
) {
    // 1) Load only this user’s cards for the set
    val userId = FirebaseAuth.getInstance().currentUser?.uid
        ?: return
    val cards by flashcardDao
        .getFlashcardsForSet(setName, userId)
        .collectAsState(initial = emptyList())

    // 2) Provide calendar context and state
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    // 3) If empty, show a simple message and bail out
    if (cards.isEmpty()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = { Text(setName) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor    = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No cards in this set yet.", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    // 4) Otherwise we have at least one card—safe to index
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(setName) },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.Event, contentDescription = "Schedule Test")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val listState: LazyListState = rememberLazyListState()

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top    = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start  = 16.dp,
                end    = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                var currentIndex by remember { mutableStateOf(0) }
                var showAnswer   by remember { mutableStateOf(false) }
                val card = cards[currentIndex]  // safe: cards has ≥1 element

                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { showAnswer = !showAnswer },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text    = if (showAnswer) card.answer else card.question,
                            style   = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                showAnswer = false
                            }
                        },
                        enabled = currentIndex > 0
                    ) { Text("Previous") }

                    Text("${currentIndex + 1} of ${cards.size}")

                    Button(
                        onClick = {
                            if (currentIndex < cards.lastIndex) {
                                currentIndex++
                                showAnswer = false
                            }
                        },
                        enabled = currentIndex < cards.lastIndex
                    ) { Text("Next") }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor   = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Set")
                }
            }
        }

        // Confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title            = { Text("Delete \"$setName\"?") },
                text             = { Text("This will remove all cards in this set. Are you sure?") },
                confirmButton    = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            flashcardDao.deleteFlashcardsForSet(setName, userId)
                            navController.popBackStack(Screen.Sets.route, false)
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton    = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Date picker for scheduling a test
        if (showDatePicker) {
            val today = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val beginTime = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 9, 0)  // default to 9 AM
                    }.timeInMillis

                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, "Test: $setName")
                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            beginTime + 60 * 60 * 1000)   // one-hour event
                        putExtra(CalendarContract.Events.DESCRIPTION,
                            "Review set \"$setName\" before test")
                    }
                    context.startActivity(intent)
                    showDatePicker = false
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}
