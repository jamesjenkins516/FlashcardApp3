// app/src/main/java/com/example/flashcardapp/screens/LoginScreen.kt

package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    // Firebase auth instance
    val auth = FirebaseAuth.getInstance()

    // Local state for email, password, and error message
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== App Title =====
            Text(
                text = "Recall-It",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    shadow        = Shadow(
                        color      = MaterialTheme.colorScheme.secondary,
                        offset     = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color     = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )

            // ===== Email Field =====
            OutlinedTextField(
                value           = email,
                onValueChange   = { email = it },
                label           = { Text("Email") },
                leadingIcon     = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier        = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ===== Password Field =====
            OutlinedTextField(
                value                 = password,
                onValueChange         = { password = it },
                label                 = { Text("Password") },
                leadingIcon           = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation  = PasswordVisualTransformation(),
                modifier              = Modifier.fillMaxWidth()
            )

            // ===== Error Message =====
            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            // ===== Login Button =====
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            navController.navigate(Screen.Sets.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        .addOnFailureListener { ex ->
                            errorMsg = ex.localizedMessage
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(8.dp))

            // ===== Navigate to Sign-Up =====
            TextButton(
                onClick = { navController.navigate(Screen.Signup.route) }
            ) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}
