package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Profile
import com.turkcell.libraryapp.data.repository.AuthRepository
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Giriş/Kayıt butonlarının durumunu yönetmek için
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

// Splash ekranında uygulamanın nereye gideceğine karar vermek için
sealed class SessionState {
    object Initializing : SessionState()
    object Unauthenticated : SessionState()
    data class Authenticated(val role: String) : SessionState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Initializing)
    val sessionState: StateFlow<SessionState> = _sessionState

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    init {

        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = repository.getCurrentUserId()
                        if (userId != null) {
                            val userProfile = repository.getProfile(userId)
                            _profile.value = userProfile

                            _sessionState.value = SessionState.Authenticated(userProfile?.role ?: "student")
                        } else {
                            _sessionState.value = SessionState.Unauthenticated
                        }
                    }
                    SessionStatus.Initializing -> {
                        _sessionState.value = SessionState.Initializing
                    }
                    else -> {
                        _profile.value = null
                        _sessionState.value = SessionState.Unauthenticated
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signIn(email, password)
                .onSuccess {
                    val userId = repository.getCurrentUserId()
                    if (userId != null) {
                        val userProfile = repository.getProfile(userId)
                        _profile.value = userProfile

                        _authState.value = AuthState.Success(userProfile?.role ?: "student")
                    } else {
                        _authState.value = AuthState.Error("Profil verisi alınamadı.")
                    }
                }
                .onFailure { ex ->
                    _authState.value = AuthState.Error(ex.message ?: "Giriş başarısız")
                }
        }
    }

    fun signUp(email: String, password: String, fullName: String, studentNo: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signUp(email, password, fullName, studentNo)
                .onSuccess {
                    _authState.value = AuthState.Success("student")
                }
                .onFailure { ex ->
                    _authState.value = AuthState.Error(ex.message ?: "Kayıt başarısız")
                }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}