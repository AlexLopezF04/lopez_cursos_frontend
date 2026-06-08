package com.alexlopez.cursosapp.presentation.ui.auth

import com.alexlopez.cursosapp.domain.model.LoggedUser

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: LoggedUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
