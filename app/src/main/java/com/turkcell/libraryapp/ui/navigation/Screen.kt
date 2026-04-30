package com.turkcell.libraryapp.ui.navigation

// Sayfa rotalarımızın tanımı.
sealed class Screen(val route: String) {
    object Login : Screen(route = "login")
    object Register : Screen(route = "register")
    object Homepage : Screen(route = "homepage")
    object Splash : Screen(route = "splash")
}