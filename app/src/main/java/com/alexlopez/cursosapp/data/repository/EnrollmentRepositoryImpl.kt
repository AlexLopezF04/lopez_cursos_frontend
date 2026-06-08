package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.EnrollmentApi
import com.alexlopez.cursosapp.data.remote.dto.*
import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.domain.model.EnrollmentPayload
import com.alexlopez.cursosapp.domain.repository.EnrollmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrollmentRepositoryImpl @Inject constructor(
    private val api: EnrollmentApi,
) : EnrollmentRepository {

    override suspend fun getEnrollments(
        page: Int,
        estado: String?,
    ): Result<Pair<List<Enrollment>, Int>> = runCatching {
        val response = api.getEnrollments(page = page, estado = estado)
        if (response.isSuccessful) {
            val body = response.body() ?: error("Empty body")
            body.results.map { it.toDomain() } to body.count
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getEnrollment(id: Int): Result<Enrollment> = runCatching {
        val response = api.getEnrollment(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createEnrollment(payload: EnrollmentPayload): Result<Enrollment> =
        runCatching {
            val response = api.createEnrollment(payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun updateEnrollment(id: Int, estado: String): Result<Enrollment> =
        runCatching {
            val response = api.updateEnrollment(id, EnrollmentUpdateDto(estado))
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deleteEnrollment(id: Int): Result<Unit> = runCatching {
        val response = api.deleteEnrollment(id)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
