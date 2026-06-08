package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.model.ProgressPayload

interface ProgressRepository {
    suspend fun getProgressList(matriculaId: Int?): Result<List<Progress>>
    suspend fun getProgress(id: Int): Result<Progress>
    suspend fun createProgress(payload: ProgressPayload): Result<Progress>
    suspend fun updateProgress(id: Int, completada: Boolean): Result<Progress>
    suspend fun deleteProgress(id: Int): Result<Unit>
}
