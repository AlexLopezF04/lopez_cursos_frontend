package com.alexlopez.cursosapp.domain.model

data class Lesson(
    val id: Int,
    val cursoId: Int,
    val titulo: String,
    val contenido: String,
    val videoUrl: String,
    val orden: Int,
    val duracionMin: Int,
)

data class LessonPayload(
    val titulo: String,
    val contenido: String,
    val videoUrl: String = "",
    val orden: Int = 0,
    val duracionMin: Int = 0,
)
