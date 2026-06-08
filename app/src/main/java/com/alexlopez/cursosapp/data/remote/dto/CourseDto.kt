package com.alexlopez.cursosapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.domain.model.CoursePayload

data class CursoInstructorDto(
    val id:       Int,
    val username: String,
)

data class CursoCategoriaDto(
    val id:     Int,
    val nombre: String,
)

data class CourseDto(
    val id:          Int,
    val titulo:      String,
    val descripcion: String,
    val precio:      String,
    val nivel:       String,
    val publicado:   Boolean,
    val instructor:  CursoInstructorDto?,
    val categoria:   CursoCategoriaDto?,
    @SerializedName("categoria_id") val categoriaId: Int?,
    @SerializedName("created_at")   val createdAt:   String,
    @SerializedName("updated_at")   val updatedAt:   String,
)

data class CourseRequestDto(
    val titulo:       String,
    val descripcion:  String,
    val precio:       Double,
    val nivel:        String,
    val publicado:    Boolean,
    @SerializedName("categoria_id") val categoriaId: Int?,
)

fun CourseDto.toDomain() = Course(
    id                = id,
    titulo            = titulo,
    descripcion       = descripcion,
    precio            = precio.toDoubleOrNull() ?: 0.0,
    nivel             = nivel,
    publicado         = publicado,
    instructorId      = instructor?.id ?: 0,
    instructorNombre  = instructor?.username ?: "",
    categoriaId       = categoriaId ?: categoria?.id,
    categoriaNombre   = categoria?.nombre,
    leccionesCount    = 0,
    createdAt         = createdAt,
    updatedAt         = updatedAt,
)

fun CoursePayload.toRequest() = CourseRequestDto(
    titulo      = titulo,
    descripcion = descripcion,
    precio      = precio,
    nivel       = nivel,
    publicado   = publicado,
    categoriaId = categoriaId,
)
