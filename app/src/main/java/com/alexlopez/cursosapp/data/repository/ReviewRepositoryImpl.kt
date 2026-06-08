package com.alexlopez.cursosapp.data.repository

import com.alexlopez.cursosapp.data.remote.api.ReviewApi
import com.alexlopez.cursosapp.data.remote.dto.toDomain
import com.alexlopez.cursosapp.data.remote.dto.toRequest
import com.alexlopez.cursosapp.domain.model.Review
import com.alexlopez.cursosapp.domain.model.ReviewPayload
import com.alexlopez.cursosapp.domain.repository.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val api: ReviewApi,
) : ReviewRepository {

    override suspend fun getReviews(cursoId: Int): Result<List<Review>> = runCatching {
        val response = api.getReviews(cursoId)
        if (response.isSuccessful) {
            (response.body() ?: error("Empty body")).map { it.toDomain() }
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getReview(cursoId: Int, reviewId: Int): Result<Review> = runCatching {
        val response = api.getReview(cursoId, reviewId)
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}")
    }

    override suspend fun createReview(cursoId: Int, payload: ReviewPayload): Result<Review> =
        runCatching {
            val response = api.createReview(cursoId, payload.toRequest())
            if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun updateReview(
        cursoId: Int,
        reviewId: Int,
        payload: ReviewPayload,
    ): Result<Review> = runCatching {
        val response = api.updateReview(cursoId, reviewId, payload.toRequest())
        if (response.isSuccessful) response.body()?.toDomain() ?: error("Empty body")
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteReview(cursoId: Int, reviewId: Int): Result<Unit> = runCatching {
        val response = api.deleteReview(cursoId, reviewId)
        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }
}
