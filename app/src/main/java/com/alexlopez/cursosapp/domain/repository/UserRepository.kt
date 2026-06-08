package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.User
import com.alexlopez.cursosapp.domain.model.UserPayload

interface UserRepository {
    suspend fun getUsers(page: Int = 1, search: String? = null): Result<Pair<List<User>, Int>>
    suspend fun getUser(id: Int): Result<User>
    suspend fun getMyProfile(): Result<User>
    suspend fun updateMyProfile(payload: UserPayload): Result<User>
    suspend fun createUser(payload: UserPayload): Result<User>
    suspend fun updateUser(id: Int, payload: UserPayload): Result<User>
    suspend fun deleteUser(id: Int): Result<Unit>
}
