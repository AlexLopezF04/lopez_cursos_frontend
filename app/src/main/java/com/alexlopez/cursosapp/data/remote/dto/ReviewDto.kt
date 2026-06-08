package com.alexlopez.cursosapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.alexlopez.cursosapp.domain.model.Review
import com.alexlopez.cursosapp.domain.model.ReviewPayload

data class ReviewDto(
    val id:               Int,
    val usuario:          Int,
    @SerializedName("usuario_nombre") val usuarioNombre: String,
    val curso:            Int,
    val calificacion:     Int,
    val comentario:       String,
    @SerializedName("created_at") val createdAt: String,
)

data class ReviewRequestDto(
    val calificacion: Int,
    val comentario:   String = "",
)

fun ReviewDto.toDomain() = Review(
    id             = id,
    usuarioId      = usuario,
    usuarioNombre  = usuarioNombre,
    cursoId        = curso,
    calificacion   = calificacion,
    comentario     = comentario,
    createdAt      = createdAt,
)

fun ReviewPayload.toRequest() = ReviewRequestDto(
    calificacion = calificacion,
    comentario   = comentario,
)
