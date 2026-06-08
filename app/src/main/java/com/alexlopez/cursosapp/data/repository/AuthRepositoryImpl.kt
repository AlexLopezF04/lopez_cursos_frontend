package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.local.TokenDataStore
import com.alexlopez.cursosapp.data.remote.api.AuthApi
import com.alexlopez.cursosapp.data.remote.dto.*
import com.alexlopez.cursosapp.domain.model.LoggedUser
import com.alexlopez.cursosapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api:            AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoggedUser> =
        runCatching {
            val response = api.login(LoginRequest(username, password))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: ""
                error(parseErrorMessage(errorBody, response.code()))
            }
            val body = response.body() ?: error("Empty body")
            tokenDataStore.saveTokens(body.access, body.refresh)
            tokenDataStore.saveUser(body.userId, body.username, body.email, body.rol)
            LoggedUser(body.userId, body.username, body.email, body.rol)
        }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        rol: String,
    ): Result<LoggedUser> = runCatching {
        val response = api.register(RegisterRequest(username, email, password, rol))
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: ""
            error(parseErrorMessage(errorBody, response.code()))
        }
        val body = response.body()!!
        tokenDataStore.saveTokens(body.access, body.refresh)
        tokenDataStore.saveUser(body.userId, body.username, body.email, body.rol)
        LoggedUser(body.userId, body.username, body.email, body.rol)
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        val refresh = tokenDataStore.getRefreshToken()
        if (refresh != null) {
            runCatching { api.logout(LogoutRequest(refresh)) }
        }
        tokenDataStore.clearSession()
    }

    override suspend fun getStoredUser(): TokenDataStore.UserSnapshot? =
        tokenDataStore.userSnapshot.first()

    override suspend fun isLoggedIn(): Boolean =
        !tokenDataStore.getAccessToken().isNullOrBlank()

    private fun parseErrorMessage(body: String, code: Int): String {
        return try {
            val map = com.google.gson.Gson()
                .fromJson(body, Map::class.java)
            map["detail"]?.toString()
                ?: map["non_field_errors"]?.toString()
                ?: map.values.firstOrNull()?.toString()
                ?: "Error $code"
        } catch (e: Exception) {
            "Error $code"
        }
    }
}
