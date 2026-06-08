package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CategoryApi {
    @GET("categorias/")
    suspend fun getCategories(): Response<PaginatedDto<CategoryDto>>

    @GET("categorias/{id}/")
    suspend fun getCategory(@Path("id") id: Int): Response<CategoryDto>

    @POST("categorias/")
    suspend fun createCategory(@Body body: CategoryRequestDto): Response<CategoryDto>

    @PATCH("categorias/{id}/")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body body: CategoryRequestDto,
    ): Response<CategoryDto>

    @DELETE("categorias/{id}/")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Unit>
}
