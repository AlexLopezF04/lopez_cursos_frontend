package com.alexlopez.cursosapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.alexlopez.cursosapp.domain.model.User
import com.alexlopez.cursosapp.domain.model.UserPayload

data class UserDto(
    val id:         Int,
    val username:   String,
    val email:      String,
    @SerializedName("first_name")  val firstName:  String,
    @SerializedName("last_name")   val lastName:   String,
    val rol:        String,
    val bio:        String,
    val foto:       String?,
    @SerializedName("is_staff")    val isStaff:    Boolean,
    @SerializedName("is_active")   val isActive:   Boolean,
    @SerializedName("created_at")  val createdAt:  String,
)

data class UserRequestDto(
    val username:   String,
    val email:      String,
    val password:   String? = null,
    val rol:        String? = null,
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name")  val lastName:  String = "",
    val bio:        String = "",
)

fun UserDto.toDomain() = User(
    id        = id,
    username  = username,
    email     = email,
    firstName = firstName,
    lastName  = lastName,
    rol       = rol,
    bio       = bio,
    fotoUrl   = foto,
    isActive  = isActive,
    isStaff   = isStaff,
    createdAt = createdAt,
)

fun UserPayload.toRequest() = UserRequestDto(
    username  = username,
    email     = email,
    password  = password,
    rol       = rol,
    firstName = firstName,
    lastName  = lastName,
    bio       = bio,
)
