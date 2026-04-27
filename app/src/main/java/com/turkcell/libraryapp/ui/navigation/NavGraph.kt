package com.turkcell.libraryapp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.halit.ui.screen.auth.RegisterScreen
import com.turkcell.libraryapp.ui.screen.LoginScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Register.route)
    {
        composable(Screen.Login.route) { LoginScreen() }
        composable(Screen.Register.route) { RegisterScreen() }
    }
}