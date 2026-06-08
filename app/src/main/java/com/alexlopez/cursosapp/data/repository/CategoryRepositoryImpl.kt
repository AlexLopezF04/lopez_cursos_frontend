package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.CategoryApi
import com.alexlopez.cursosapp.data.remote.dto.toDomain
import com.alexlopez.cursosapp.data.remote.dto.toRequest
import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.CategoryPayload
import com.alexlopez.cursosapp.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi,
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        val response = api.getCategories()
        if (response.isSuccessful) {
            (response.body() ?: error("Empty body")).results.map { it.toDomain() }
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getCategory(id: Int): Result<Category> = runCatching {
        val response = api.getCategory(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createCategory(payload: CategoryPayload): Result<Category> = runCatching {
        val response = api.createCategory(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateCategory(id: Int, payload: CategoryPayload): Result<Category> =
        runCatching {
            val response = api.updateCategory(id, payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deleteCategory(id: Int): Result<Unit> = runCatching {
        val response = api.deleteCategory(id)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
