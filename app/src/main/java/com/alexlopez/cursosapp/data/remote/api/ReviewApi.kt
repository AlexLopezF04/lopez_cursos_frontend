package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReviewApi {
    @GET("cursos/{curso_pk}/resenas/")
    suspend fun getReviews(@Path("curso_pk") cursoId: Int): Response<List<ReviewDto>>

    @GET("cursos/{curso_pk}/resenas/{id}/")
    suspend fun getReview(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
    ): Response<ReviewDto>

    @POST("cursos/{curso_pk}/resenas/")
    suspend fun createReview(
        @Path("curso_pk") cursoId: Int,
        @Body body: ReviewRequestDto,
    ): Response<ReviewDto>

    @PATCH("cursos/{curso_pk}/resenas/{id}/")
    suspend fun updateReview(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
        @Body body: ReviewRequestDto,
    ): Response<ReviewDto>

    @DELETE("cursos/{curso_pk}/resenas/{id}/")
    suspend fun deleteReview(
        @Path("curso_pk") cursoId: Int,
        @Path("id") id: Int,
    ): Response<Unit>
}
