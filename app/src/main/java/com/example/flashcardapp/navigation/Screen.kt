package com.example.flashcardapp.navigation



//Navigation routes
sealed class Screen(val route: String, val label: String) {
    object Login     : Screen("login",               "Login")
    object Signup    : Screen("signup",              "Sign Up")
    object Sets      : Screen("home",                "Sets")
    object CreateSet : Screen("create",              "Create")
    object Learn     : Screen("learn",               "Learn")
    object LearnQuiz : Screen("learn/{setName}",     "Quiz")
    object SetDetail : Screen("setDetail/{setName}", "Detail")
    object EditSet   : Screen("edit/{setName}",      "Edit")
    object Settings  : Screen("settings",            "Settings")
}
