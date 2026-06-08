package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.UserApi
import com.alexlopez.cursosapp.data.remote.dto.*
import com.alexlopez.cursosapp.domain.model.User
import com.alexlopez.cursosapp.domain.model.UserPayload
import com.alexlopez.cursosapp.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
) : UserRepository {

    override suspend fun getUsers(
        page: Int,
        search: String?,
    ): Result<Pair<List<User>, Int>> = runCatching {
        val response = api.getUsers(search = search, page = page)
        if (response.isSuccessful) {
            val body = response.body() ?: error("Empty body")
            body.results.map { it.toDomain() } to body.count
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getUser(id: Int): Result<User> = runCatching {
        val response = api.getUser(id)
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}")
    }

    override suspend fun getMyProfile(): Result<User> = runCatching {
        val response = api.getMyProfile()
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateMyProfile(payload: UserPayload): Result<User> = runCatching {
        val response = api.updateMyProfile(payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun createUser(payload: UserPayload): Result<User> = runCatching {
        val response = api.createUser(payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateUser(id: Int, payload: UserPayload): Result<User> = runCatching {
        val response = api.updateUser(id, payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteUser(id: Int): Result<Unit> = runCatching {
        val response = api.deleteUser(id)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
