package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.domain.model.EnrollmentPayload

interface EnrollmentRepository {
    suspend fun getEnrollments(page: Int = 1, estado: String? = null): Result<Pair<List<Enrollment>, Int>>
    suspend fun getEnrollment(id: Int): Result<Enrollment>
    suspend fun createEnrollment(payload: EnrollmentPayload): Result<Enrollment>
    suspend fun updateEnrollment(id: Int, estado: String): Result<Enrollment>
    suspend fun deleteEnrollment(id: Int): Result<Unit>
}
