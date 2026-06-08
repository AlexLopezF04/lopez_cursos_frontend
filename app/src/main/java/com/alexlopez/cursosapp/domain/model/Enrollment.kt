package com.alexlopez.cursosapp.domain.model

enum class EnrollmentStatus(val value: String, val label: String) {
    ACTIVA("activa", "Activa"),
    VENCIDA("vencida", "Vencida"),
    CANCELADA("cancelada", "Cancelada");

    companion object {
        fun fromValue(value: String): EnrollmentStatus =
            entries.firstOrNull { it.value == value } ?: ACTIVA
    }
}

data class Enrollment(
    val id: Int,
    val usuarioId: Int,
    val usuarioEmail: String,
    val cursoId: Int,
    val cursoTitulo: String,
    val fechaPago: String,
    val montoPagado: Double,
    val estado: EnrollmentStatus,
)

data class EnrollmentPayload(
    val cursoId: Int,
    val montoPagado: Double,
)
