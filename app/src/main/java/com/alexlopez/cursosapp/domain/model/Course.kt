package com.alexlopez.cursosapp.domain.model

data class Course(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val nivel: String,
    val publicado: Boolean,
    val instructorId: Int,
    val instructorNombre: String,
    val categoriaId: Int?,
    val categoriaNombre: String?,
    val leccionesCount: Int,
    val createdAt: String,
    val updatedAt: String,
)

data class CoursePayload(
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val nivel: String,
    val publicado: Boolean,
    val categoriaId: Int?,
)

data class CourseFilters(
    val search: String? = null,
    val nivel: String? = null,
    val categoria: Int? = null,
    val precioMin: Double? = null,
    val precioMax: Double? = null,
    val publicado: Boolean? = null,
    val ordering: String? = null,
    val page: Int = 1,
)
