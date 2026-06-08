package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.data.local.TokenDataStore
import com.alexlopez.cursosapp.domain.model.LoggedUser
import com.alexlopez.cursosapp.domain.repository.AuthRepository
import com.alexlopez.cursosapp.presentation.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<LoggedUser?>(null)
    val currentUser: StateFlow<LoggedUser?> = _currentUser.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isAdmin: StateFlow<Boolean> = _currentUser
        .map { it?.rol == "admin" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isInstructor: StateFlow<Boolean> = _currentUser
        .map { it?.rol == "instructor" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val canManageContent: StateFlow<Boolean> = _currentUser
        .map { it?.rol == "admin" || it?.rol == "instructor" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    init {
        viewModelScope.launch {
            tokenDataStore.sessionExpired.collect {
                _currentUser.value = null
                _uiState.value = AuthUiState.Idle
            }
        }
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            try {
                val snapshot = authRepository.getStoredUser()
                if (snapshot != null && authRepository.isLoggedIn()) {
                    _currentUser.value = LoggedUser(
                        id       = snapshot.id,
                        username = snapshot.username,
                        email    = snapshot.email,
                        rol      = snapshot.rol,
                    )
                }
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    fun login(username: String, password: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(username.trim(), password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al iniciar sesi\u00f3n")
                }
        }
    }

    fun register(username: String, email: String, password: String, rol: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(username.trim(), email.trim(), password, rol)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al registrarse")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _uiState.value     = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
