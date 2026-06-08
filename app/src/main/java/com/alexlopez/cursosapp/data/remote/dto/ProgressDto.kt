package com.alexlopez.cursosapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.model.ProgressPayload

data class ProgressDto(
    val id:               Int,
    val matricula:        Int,
    val leccion:          Int,
    @SerializedName("leccion_titulo")  val leccionTitulo: String?,
    @SerializedName("curso_id")        val cursoId: Int?,
    @SerializedName("curso_titulo")    val cursoTitulo: String?,
    val completada:       Boolean,
    @SerializedName("fecha_completado") val fechaCompletado: String?,
)

data class ProgressRequestDto(
    val matricula:  Int,
    val leccion:    Int,
    val completada: Boolean = false,
)

data class ProgressUpdateDto(
    val completada: Boolean,
)

fun ProgressDto.toDomain() = Progress(
    id              = id,
    matriculaId     = matricula,
    leccionId       = leccion,
    leccionTitulo   = leccionTitulo,
    cursoId         = cursoId,
    cursoTitulo     = cursoTitulo,
    completada      = completada,
    fechaCompletado = fechaCompletado,
)

fun ProgressPayload.toRequest() = ProgressRequestDto(
    matricula  = matriculaId,
    leccion    = leccionId,
    completada = completada,
)
