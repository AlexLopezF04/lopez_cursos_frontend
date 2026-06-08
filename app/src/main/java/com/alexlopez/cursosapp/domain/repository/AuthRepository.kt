package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.data.local.TokenDataStore
import com.alexlopez.cursosapp.domain.model.LoggedUser

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoggedUser>
    suspend fun register(username: String, email: String, password: String, rol: String): Result<LoggedUser>
    suspend fun logout(): Result<Unit>
    suspend fun getStoredUser(): TokenDataStore.UserSnapshot?
    suspend fun isLoggedIn(): Boolean
}
