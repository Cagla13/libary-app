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
    // ViewModel'deki oturum durumunu takip ediyoruz
    val sessionState by authViewModel.sessionState.collectAsState()

    // Durum değiştikçe yönlendirme yapacak olan LaunchedEffect
    LaunchedEffect(sessionState) {
        when (val state = sessionState) {
            is SessionState.Authenticated -> {
                // Eğer giriş yapılmışsa direkt ana sayfaya git
                onNavigateToHome(state.role)
            }
            is SessionState.Unauthenticated -> {
                // Giriş yapılmamışsa login ekranına git
                onNavigateToLogin()
            }
            is SessionState.Initializing -> {
                // Henüz kontrol ediliyor, hiçbir şey yapma (Splash'ta kal)
            }
        }
    }

    // Splash Ekranı Görseli (Loading)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}