package com.alexlopez.cursosapp.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val rol: String,
    val bio: String,
    val fotoUrl: String?,
    val isActive: Boolean,
    val isStaff: Boolean,
    val createdAt: String,
)

data class UserPayload(
    val username: String,
    val email: String,
    val password: String? = null,
    val rol: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val bio: String = "",
)
