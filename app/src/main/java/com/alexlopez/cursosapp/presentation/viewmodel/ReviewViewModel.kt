package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Review
import com.alexlopez.cursosapp.domain.model.ReviewPayload
import com.alexlopez.cursosapp.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingReview: Review? = null,
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun loadReviews(cursoId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            reviewRepository.getReviews(cursoId)
                .onSuccess { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun createReview(cursoId: Int, payload: ReviewPayload) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            reviewRepository.createReview(cursoId, payload)
                .onSuccess { loadReviews(cursoId) }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateReview(cursoId: Int, reviewId: Int, payload: ReviewPayload) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            reviewRepository.updateReview(cursoId, reviewId, payload)
                .onSuccess { loadReviews(cursoId) }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteReview(cursoId: Int, reviewId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            reviewRepository.deleteReview(cursoId, reviewId)
                .onSuccess { loadReviews(cursoId) }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(showForm = true, editingReview = null) }
    }

    fun showEditForm(review: Review) {
        _uiState.update { it.copy(showForm = true, editingReview = review) }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingReview = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
