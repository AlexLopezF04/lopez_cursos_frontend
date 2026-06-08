package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface EnrollmentApi {
    @GET("matriculas/")
    suspend fun getEnrollments(
        @Query("page")   page:   Int?    = null,
        @Query("estado") estado: String? = null,
    ): Response<PaginatedDto<EnrollmentDto>>

    @GET("matriculas/{id}/")
    suspend fun getEnrollment(@Path("id") id: Int): Response<EnrollmentDto>

    @POST("matriculas/")
    suspend fun createEnrollment(@Body body: EnrollmentRequestDto): Response<EnrollmentDto>

    @PATCH("matriculas/{id}/")
    suspend fun updateEnrollment(
        @Path("id") id: Int,
        @Body body: EnrollmentUpdateDto,
    ): Response<EnrollmentDto>

    @DELETE("matriculas/{id}/")
    suspend fun deleteEnrollment(@Path("id") id: Int): Response<Unit>
}
