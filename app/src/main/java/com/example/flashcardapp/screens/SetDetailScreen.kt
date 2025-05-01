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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
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
    //Log in with firebase and get cards
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val cards by flashcardDao
        .getFlashcardsForSet(setName, userId)
        .collectAsState(initial = emptyList())

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    //UI state
    var showDatePicker   by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Track which card to show, and whether it's flipped
    var currentIndex by remember { mutableStateOf(0) }
    var showAnswer   by remember { mutableStateOf(false) }


    LaunchedEffect(cards.size) {
        currentIndex = currentIndex.coerceIn(
            0,
            (cards.lastIndex).coerceAtLeast(0)
        )
    }

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
        if (cards.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No cards in this set yet.", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val listState: LazyListState = rememberLazyListState()
            LazyColumn(
                state          = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier       = Modifier.fillMaxSize()
            ) {
                item {
                    val card = cards.getOrNull(currentIndex) ?: return@item

                    //Flip card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { showAnswer = !showAnswer },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            Modifier.padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = if (showAnswer) card.answer else card.question,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    //Prev / Next card
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { currentIndex-- ; showAnswer = false },
                            enabled = currentIndex > 0
                        ) { Text("Previous") }

                        Text("${currentIndex + 1} of ${cards.size}")

                        Button(
                            onClick = { currentIndex++ ; showAnswer = false },
                            enabled = currentIndex < cards.lastIndex
                        ) { Text("Next") }
                    }

                    Spacer(Modifier.height(24.dp))

                    //Delete Set and Edit Set buttons
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor   = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Delete Set")
                        }

                        OutlinedButton(onClick = {
                            navController.navigate(
                                Screen.EditSet.route.replace("{setName}", setName)
                            )
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit Set")
                        }
                    }
                }
            }

            //Delete confirmation dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title            = { Text("Delete \"$setName\"?") },
                    text             = { Text("This will remove all cards in this set. Are you sure?") },
                    confirmButton    = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            navController.popBackStack(Screen.Sets.route, false)
                            scope.launch {
                                flashcardDao.deleteFlashcardsForSet(setName, userId)
                            }
                        }) { Text("Delete") }
                    },
                    dismissButton    = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            //DatePicker for scheduling
            if (showDatePicker) {
                val today = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val start = Calendar.getInstance().apply {
                            set(y, m, d, 9, 0)
                        }.timeInMillis
                        Intent(Intent.ACTION_INSERT).also {
                            it.data = CalendarContract.Events.CONTENT_URI
                            it.putExtra(CalendarContract.Events.TITLE, "Test: $setName")
                            it.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
                            it.putExtra(
                                CalendarContract.EXTRA_EVENT_END_TIME,
                                start + 60*60*1000
                            )
                            it.putExtra(
                                CalendarContract.Events.DESCRIPTION,
                                "Review set \"$setName\" before test"
                            )
                            context.startActivity(it)
                        }
                        showDatePicker = false
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }
}
