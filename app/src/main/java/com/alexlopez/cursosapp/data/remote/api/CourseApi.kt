package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CourseApi {
    @GET("cursos/")
    suspend fun getCourses(
        @Query("search")     search:    String? = null,
        @Query("nivel")      nivel:     String? = null,
        @Query("categoria")  categoria: Int?    = null,
        @Query("precio_min") precioMin: Double? = null,
        @Query("precio_max") precioMax: Double? = null,
        @Query("publicado")  publicado: Boolean? = null,
        @Query("ordering")   ordering:  String? = null,
        @Query("page")       page:      Int?    = null,
    ): Response<PaginatedDto<CourseDto>>

    @GET("cursos/{id}/")
    suspend fun getCourse(@Path("id") id: Int): Response<CourseDto>

    @POST("cursos/")
    suspend fun createCourse(@Body body: CourseRequestDto): Response<CourseDto>

    @PATCH("cursos/{id}/")
    suspend fun updateCourse(
        @Path("id") id: Int,
        @Body body: CourseRequestDto,
    ): Response<CourseDto>

    @DELETE("cursos/{id}/")
    suspend fun deleteCourse(@Path("id") id: Int): Response<Unit>
}
