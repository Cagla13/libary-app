package com.turkcell.libraryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkcell.libraryapp.ui.screen.* // Tüm ekranları kapsar
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()
    val bookViewModel: BookViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // SPLASH EKRANI
        composable(Screen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Homepage.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // GİRİŞ EKRANI
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
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
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // ANASAYFA
        composable(Screen.Homepage.route) {
            HomeScreen(
                authViewModel = authViewModel,
                bookViewModel = bookViewModel,
                onNavigateToBorrows = {
                    navController.navigate(Screen.MyBorrows.route)
                }
            )
        }

        // KİRALAMALARIM SAYFASI
        composable(Screen.MyBorrows.route) {
            MyBorrowsScreen(
                authViewModel = authViewModel,
                bookViewModel = bookViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MyBorrowsScreen(
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    onBackClick: () -> Boolean
) {
    TODO("Not yet implemented")
}