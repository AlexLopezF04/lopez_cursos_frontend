package com.alexlopez.cursosapp.data.remote.dto

import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.CategoryPayload

data class CategoryDto(
    val id:     Int,
    val nombre: String,
    val slug:   String,
)

data class CategoryRequestDto(
    val nombre: String,
    val slug:   String,
)

fun CategoryDto.toDomain() = Category(
    id     = id,
    nombre = nombre,
    slug   = slug,
)

fun CategoryPayload.toRequest() = CategoryRequestDto(
    nombre = nombre,
    slug   = slug,
)
