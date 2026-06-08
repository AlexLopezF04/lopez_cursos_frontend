package com.alexlopez.cursosapp.domain.model

data class Review(
    val id: Int,
    val usuarioId: Int,
    val usuarioNombre: String,
    val cursoId: Int,
    val calificacion: Int,
    val comentario: String,
    val createdAt: String,
)

data class ReviewPayload(
    val calificacion: Int,
    val comentario: String = "",
)
