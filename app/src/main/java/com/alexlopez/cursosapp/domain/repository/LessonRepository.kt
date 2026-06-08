package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.LessonPayload

interface LessonRepository {
    suspend fun getLessons(cursoId: Int): Result<List<Lesson>>
    suspend fun getLesson(cursoId: Int, lessonId: Int): Result<Lesson>
    suspend fun createLesson(cursoId: Int, payload: LessonPayload): Result<Lesson>
    suspend fun updateLesson(cursoId: Int, lessonId: Int, payload: LessonPayload): Result<Lesson>
    suspend fun deleteLesson(cursoId: Int, lessonId: Int): Result<Unit>
}
