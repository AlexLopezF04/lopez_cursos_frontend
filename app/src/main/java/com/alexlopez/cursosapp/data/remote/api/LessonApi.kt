package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface LessonApi {
    @GET("cursos/{curso_pk}/lecciones/")
    suspend fun getLessons(@Path("curso_pk") cursoId: Int): Response<List<LessonDto>>

    @GET("cursos/{curso_pk}/lecciones/{id}/")
    suspend fun getLesson(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
    ): Response<LessonDto>

    @POST("cursos/{curso_pk}/lecciones/")
    suspend fun createLesson(
        @Path("curso_pk") cursoId: Int,
        @Body body: LessonRequestDto,
    ): Response<LessonDto>

    @PATCH("cursos/{curso_pk}/lecciones/{id}/")
    suspend fun updateLesson(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
        @Body body: LessonRequestDto,
    ): Response<LessonDto>

    @DELETE("cursos/{curso_pk}/lecciones/{id}/")
    suspend fun deleteLesson(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
    ): Response<Unit>
}
