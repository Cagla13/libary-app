package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.SessionState

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {

    val sessionState by authViewModel.sessionState.collectAsState()


    LaunchedEffect(sessionState) {
        when (val state = sessionState) {
            is SessionState.Authenticated -> {

                onNavigateToHome(state.role)
            }
            is SessionState.Unauthenticated -> {

                onNavigateToLogin()
            }
            is SessionState.Initializing -> {

            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}