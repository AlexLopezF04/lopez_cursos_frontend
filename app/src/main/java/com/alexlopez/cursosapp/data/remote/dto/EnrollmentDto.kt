package com.alexlopez.cursosapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.domain.model.EnrollmentPayload
import com.alexlopez.cursosapp.domain.model.EnrollmentStatus

data class EnrollmentDto(
    val id:           Int,
    val usuario:      Int,
    @SerializedName("usuario_email") val usuarioEmail: String,
    val curso:        Int,
    @SerializedName("curso_titulo")  val cursoTitulo:  String,
    @SerializedName("fecha_pago")    val fechaPago:    String,
    @SerializedName("monto_pagado")  val montoPagado:  String,
    val estado:       String,
)

data class EnrollmentRequestDto(
    val curso:        Int,
    @SerializedName("monto_pagado") val montoPagado: Double,
)

data class EnrollmentUpdateDto(
    val estado: String,
)

fun EnrollmentDto.toDomain() = Enrollment(
    id             = id,
    usuarioId      = usuario,
    usuarioEmail   = usuarioEmail,
    cursoId        = curso,
    cursoTitulo    = cursoTitulo,
    fechaPago      = fechaPago,
    montoPagado    = montoPagado.toDoubleOrNull() ?: 0.0,
    estado         = EnrollmentStatus.fromValue(estado),
)

fun EnrollmentPayload.toRequest() = EnrollmentRequestDto(
    curso        = cursoId,
    montoPagado  = montoPagado,
)
