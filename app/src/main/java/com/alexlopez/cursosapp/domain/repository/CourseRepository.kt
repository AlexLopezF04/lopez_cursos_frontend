package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.domain.model.CourseFilters
import com.alexlopez.cursosapp.domain.model.CoursePayload

interface CourseRepository {
    suspend fun getCourses(filters: CourseFilters): Result<Pair<List<Course>, Int>>
    suspend fun getCourse(id: Int): Result<Course>
    suspend fun createCourse(payload: CoursePayload): Result<Course>
    suspend fun updateCourse(id: Int, payload: CoursePayload): Result<Course>
    suspend fun deleteCourse(id: Int): Result<Unit>
}
