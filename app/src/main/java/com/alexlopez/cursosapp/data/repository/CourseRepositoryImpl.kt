package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.CourseApi
import com.alexlopez.cursosapp.data.remote.dto.toDomain
import com.alexlopez.cursosapp.data.remote.dto.toRequest
import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.domain.model.CourseFilters
import com.alexlopez.cursosapp.domain.model.CoursePayload
import com.alexlopez.cursosapp.domain.repository.CourseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val api: CourseApi,
) : CourseRepository {

    override suspend fun getCourses(filters: CourseFilters): Result<Pair<List<Course>, Int>> =
        runCatching {
            val response = api.getCourses(
                search    = filters.search,
                nivel     = filters.nivel,
                categoria = filters.categoria,
                precioMin = filters.precioMin,
                precioMax = filters.precioMax,
                publicado = filters.publicado,
                ordering  = filters.ordering,
                page      = filters.page,
            )
            if (response.isSuccessful) {
                val body = response.body() ?: error("Empty body")
                body.results.map { it.toDomain() } to body.count
            } else {
                error("Error ${response.code()}: ${response.errorBody()?.string()}")
            }
        }

    override suspend fun getCourse(id: Int): Result<Course> = runCatching {
        val response = api.getCourse(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createCourse(payload: CoursePayload): Result<Course> = runCatching {
        val response = api.createCourse(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateCourse(id: Int, payload: CoursePayload): Result<Course> =
        runCatching {
            val response = api.updateCourse(id, payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deleteCourse(id: Int): Result<Unit> = runCatching {
        val response = api.deleteCourse(id)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
