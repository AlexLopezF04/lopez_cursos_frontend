package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.CategoryPayload

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCategory(id: Int): Result<Category>
    suspend fun createCategory(payload: CategoryPayload): Result<Category>
    suspend fun updateCategory(id: Int, payload: CategoryPayload): Result<Category>
    suspend fun deleteCategory(id: Int): Result<Unit>
}
