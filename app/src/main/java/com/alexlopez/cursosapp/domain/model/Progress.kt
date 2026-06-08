package com.alexlopez.cursosapp.domain.model

data class Progress(
    val id: Int,
    val matriculaId: Int,
    val leccionId: Int,
    val leccionTitulo: String? = null,
    val cursoId: Int? = null,
    val cursoTitulo: String? = null,
    val completada: Boolean,
    val fechaCompletado: String?,
)

data class ProgressPayload(
    val matriculaId: Int,
    val leccionId: Int,
    val completada: Boolean = false,
)
