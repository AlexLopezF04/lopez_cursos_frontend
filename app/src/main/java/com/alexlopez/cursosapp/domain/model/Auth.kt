package com.alexlopez.cursosapp.domain.model

data class AuthTokens(
    val access: String,
    val refresh: String,
)

data class LoggedUser(
    val id: Int,
    val username: String,
    val email: String,
    val rol: String,
)
