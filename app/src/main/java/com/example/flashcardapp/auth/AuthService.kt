package com.example.flashcardapp.auth

import com.google.firebase.auth.FirebaseAuth

object AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid


    fun signup(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun logout() {
        auth.signOut()
    }
}
