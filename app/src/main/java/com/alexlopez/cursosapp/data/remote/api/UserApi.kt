package com.alexlopez.cursosapp.data.remote.api

import com.alexlopez.cursosapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("usuarios/")
    suspend fun getUsers(
        @Query("search") search: String? = null,
        @Query("page")   page:   Int?    = null,
    ): Response<PaginatedDto<UserDto>>

    @GET("usuarios/{id}/")
    suspend fun getUser(@Path("id") id: Int): Response<UserDto>

    @POST("usuarios/")
    suspend fun createUser(@Body body: UserRequestDto): Response<UserDto>

    @PATCH("usuarios/{id}/")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: UserRequestDto,
    ): Response<UserDto>

    @DELETE("usuarios/{id}/")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @GET("usuarios/me/")
    suspend fun getMyProfile(): Response<UserDto>

    @PATCH("usuarios/me/")
    suspend fun updateMyProfile(@Body body: UserRequestDto): Response<UserDto>
}
