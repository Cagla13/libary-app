package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Profile
import com.turkcell.libraryapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Uygulamanın giriş/kayıt durumlarını temsil eden mühürlü sınıf
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Ekranın dinleyeceği durumlar
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Giriş yapan kullanıcının profil bilgisi
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // 1. Adım: Kimlik doğrulama (Giriş yap)
            repository.signIn(email, password)
                .onSuccess {
                    // 2. Adım: Giriş başarılıysa kullanıcının UID'sini al
                    val uid = repository.getCurrentUserId()

                    if (uid != null) {
                        // 3. Adım: UID ile veritabanından profil bilgilerini çek
                        val userProfile = repository.getProfile(uid)

                        if (userProfile != null) {
                            // Her şey başarılı: Profil bulundu ve set edildi
                            _profile.value = userProfile
                            _authState.value = AuthState.Success("student")
                        } else {
                            // Kullanıcı girişi var ama veritabanında profile satırı yok
                            _authState.value = AuthState.Error("Profil bilgisi veritabanında bulunamadı.")
                        }
                    } else {
                        _authState.value = AuthState.Error("Kullanıcı kimliği (UID) alınamadı.")
                    }
                }
                .onFailure { ex ->
                    // Şifre yanlış veya internet yok gibi durumlar
                    _authState.value = AuthState.Error(ex.message ?: "Giriş işlemi başarısız oldu.")
                }
        }
    }

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        studentNo: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signUp(email, password, fullName, studentNo)
                .onSuccess {

                    _authState.value = AuthState.Success("student")
                }
                .onFailure { ex ->
                    _authState.value = AuthState.Error(ex.message ?: "Kayıt işlemi başarısız.")
                }
        }
    }


    fun resetState() {
        _authState.value = AuthState.Idle
    }
}