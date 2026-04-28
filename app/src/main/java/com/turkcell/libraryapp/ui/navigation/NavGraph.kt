package com.turkcell.libraryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.halit.ui.screen.auth.RegisterScreen
import com.turkcell.libraryapp.ui.screen.HomeScreen
import com.turkcell.libraryapp.ui.screen.LoginScreen
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()
    val bookViewModel: BookViewModel = viewModel()

    // Sadece TEK bir NavHost olmalı
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // GİRİŞ EKRANI
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { role ->
                    navController.navigate(Screen.Homepage.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // KAYIT EKRANI
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                authViewModel = authViewModel
            )
        }

        // ANA SAYFA
        composable(Screen.Homepage.route) {
            HomeScreen(authViewModel, bookViewModel)
        }
    }
}

annotation class NavGraph
