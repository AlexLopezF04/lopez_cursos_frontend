package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.LessonApi
import com.alexlopez.cursosapp.data.remote.dto.toDomain
import com.alexlopez.cursosapp.data.remote.dto.toRequest
import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.LessonPayload
import com.alexlopez.cursosapp.domain.repository.LessonRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepositoryImpl @Inject constructor(
    private val api: LessonApi,
) : LessonRepository {

    override suspend fun getLessons(cursoId: Int): Result<List<Lesson>> = runCatching {
        val response = api.getLessons(cursoId)
        if (response.isSuccessful) {
            (response.body() ?: error("Empty body")).map { it.toDomain() }
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getLesson(cursoId: Int, lessonId: Int): Result<Lesson> = runCatching {
        val response = api.getLesson(cursoId, lessonId)
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}")
    }

    override suspend fun createLesson(cursoId: Int, payload: LessonPayload): Result<Lesson> =
        runCatching {
            val response = api.createLesson(cursoId, payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun updateLesson(
        cursoId: Int,
        lessonId: Int,
        payload: LessonPayload,
    ): Result<Lesson> = runCatching {
        val response = api.updateLesson(cursoId, lessonId, payload.toRequest())
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteLesson(cursoId: Int, lessonId: Int): Result<Unit> = runCatching {
        val response = api.deleteLesson(cursoId, lessonId)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
