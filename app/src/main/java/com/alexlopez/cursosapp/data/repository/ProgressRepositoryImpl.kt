package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.ProgressApi
import com.alexlopez.cursosapp.data.remote.dto.*
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.model.ProgressPayload
import com.alexlopez.cursosapp.domain.repository.ProgressRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val api: ProgressApi,
) : ProgressRepository {

    override suspend fun getProgressList(matriculaId: Int?): Result<List<Progress>> = runCatching {
        val response = api.getProgressList(matriculaId = matriculaId)
        if (response.isSuccessful) {
            (response.body() ?: error("Empty body")).results.map { it.toDomain() }
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getProgress(id: Int): Result<Progress> = runCatching {
        val response = api.getProgress(id)
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}")
    }

    override suspend fun createProgress(payload: ProgressPayload): Result<Progress> = runCatching {
        val response = api.createProgress(payload.toRequest())
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateProgress(id: Int, completada: Boolean): Result<Progress> =
        runCatching {
            val response = api.updateProgress(id, ProgressUpdateDto(completada))
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deleteProgress(id: Int): Result<Unit> = runCatching {
        val response = api.deleteProgress(id)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
