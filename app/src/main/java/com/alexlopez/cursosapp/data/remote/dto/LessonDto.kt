package com.alexlopez.cursosapp.data.remote.dto

import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.LessonPayload
import com.google.gson.annotations.SerializedName

data class LessonDto(
    val id:           Int,
    val curso:        Int,
    val titulo:       String,
    val contenido:    String,
    @SerializedName("video_url")   val videoUrl:   String? = null,
    val orden:        Int,
    @SerializedName("duracion_min") val duracionMin: Int,
)

data class LessonRequestDto(
    val titulo:       String,
    val contenido:    String,
    @SerializedName("video_url")   val videoUrl:   String = "",
    val orden:        Int = 0,
    @SerializedName("duracion_min") val duracionMin: Int = 0,
)

fun LessonDto.toDomain() = Lesson(
    id          = id,
    cursoId     = curso,
    titulo      = titulo,
    contenido   = contenido,
    videoUrl    = videoUrl ?: "",
    orden       = orden,
    duracionMin = duracionMin,
)

fun LessonPayload.toRequest() = LessonRequestDto(
    titulo      = titulo,
    contenido   = contenido,
    videoUrl    = videoUrl,
    orden       = orden,
    duracionMin = duracionMin,
)
