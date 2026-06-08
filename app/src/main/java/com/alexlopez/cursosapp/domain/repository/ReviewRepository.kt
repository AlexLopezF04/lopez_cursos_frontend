package com.alexlopez.cursosapp.domain.repository

import com.alexlopez.cursosapp.domain.model.Review
import com.alexlopez.cursosapp.domain.model.ReviewPayload

interface ReviewRepository {
    suspend fun getReviews(cursoId: Int): Result<List<Review>>
    suspend fun getReview(cursoId: Int, reviewId: Int): Result<Review>
    suspend fun createReview(cursoId: Int, payload: ReviewPayload): Result<Review>
    suspend fun updateReview(cursoId: Int, reviewId: Int, payload: ReviewPayload): Result<Review>
    suspend fun deleteReview(cursoId: Int, reviewId: Int): Result<Unit>
}
