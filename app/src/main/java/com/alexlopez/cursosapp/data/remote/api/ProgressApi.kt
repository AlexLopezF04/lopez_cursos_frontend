package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ProgressApi {
    @GET("progresos/")
    suspend fun getProgressList(
        @Query("matricula") matriculaId: Int? = null,
        @Query("page") page: Int = 1,
    ): Response<PaginatedDto<ProgressDto>>

    @GET("progresos/{id}/")
    suspend fun getProgress(@Path("id") id: Int): Response<ProgressDto>

    @POST("progresos/")
    suspend fun createProgress(@Body body: ProgressRequestDto): Response<ProgressDto>

    @PATCH("progresos/{id}/")
    suspend fun updateProgress(
        @Path("id") id: Int,
        @Body body: ProgressUpdateDto,
    ): Response<ProgressDto>

    @DELETE("progresos/{id}/")
    suspend fun deleteProgress(@Path("id") id: Int): Response<Unit>
}
