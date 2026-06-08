package com.alexlopez.cursosapp.domain.model

data class Category(
    val id: Int,
    val nombre: String,
    val slug: String,
)

data class CategoryPayload(
    val nombre: String,
    val slug: String,
)
