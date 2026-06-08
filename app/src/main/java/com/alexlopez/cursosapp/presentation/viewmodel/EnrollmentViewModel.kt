package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.domain.model.EnrollmentPayload
import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.repository.EnrollmentRepository
import com.alexlopez.cursosapp.domain.repository.LessonRepository
import com.alexlopez.cursosapp.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnrollmentListState(
    val enrollments: List<Enrollment> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = false,
    val showForm: Boolean = false,
    val editingEnrollment: Enrollment? = null,
)

data class EnrollmentDetailState(
    val enrollment: Enrollment? = null,
    val lessons: List<Lesson> = emptyList(),
    val progressList: List<Progress> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val enrollmentRepository: EnrollmentRepository,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _listState = MutableStateFlow(EnrollmentListState())
    val listState: StateFlow<EnrollmentListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(EnrollmentDetailState())
    val detailState: StateFlow<EnrollmentDetailState> = _detailState.asStateFlow()

    private val _createResult = MutableStateFlow<Result<Enrollment>?>(null)
    val createResult: StateFlow<Result<Enrollment>?> = _createResult.asStateFlow()

    init { loadEnrollments() }

    fun loadEnrollments() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
            enrollmentRepository.getEnrollments(page = 1)
                .onSuccess { (enrollments, count) ->
                    _listState.update {
                        it.copy(
                            enrollments = enrollments,
                            isLoading = false,
                            hasMore = enrollments.size < count,
                        )
                    }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadMore() {
        if (_listState.value.isLoadingMore || !_listState.value.hasMore) return
        viewModelScope.launch {
            val nextPage = _listState.value.currentPage + 1
            _listState.update { it.copy(isLoadingMore = true) }
            enrollmentRepository.getEnrollments(page = nextPage)
                .onSuccess { (enrollments, count) ->
                    _listState.update {
                        it.copy(
                            enrollments = it.enrollments + enrollments,
                            currentPage = nextPage,
                            isLoadingMore = false,
                            hasMore = it.enrollments.size + enrollments.size < count,
                        )
                    }
                }
                .onFailure { _listState.update { it.copy(isLoadingMore = false) } }
        }
    }

    fun loadEnrollmentDetail(id: Int) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            enrollmentRepository.getEnrollment(id)
                .onSuccess { enrollment ->
                    _detailState.update { it.copy(enrollment = enrollment) }
                    val lessonResult = lessonRepository.getLessons(enrollment.cursoId)
                    val progressResult = progressRepository.getProgressList(id)
                    _detailState.update {
                        it.copy(
                            lessons = lessonResult.getOrDefault(emptyList()),
                            progressList = progressResult.getOrDefault(emptyList()),
                            isLoading = false,
                        )
                    }
                }
                .onFailure { e ->
                    _detailState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun createEnrollment(cursoId: Int, montoPagado: Double) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            enrollmentRepository.createEnrollment(EnrollmentPayload(cursoId, montoPagado))
                .onSuccess {
                    _createResult.value = Result.success(it)
                    loadEnrollments()
                }
                .onFailure { e ->
                    _createResult.value = Result.failure(e)
                }
        }
    }

    fun markLessonComplete(progressId: Int, completada: Boolean) {
        viewModelScope.launch {
            progressRepository.updateProgress(progressId, completada)
                .onSuccess { _detailState.value.enrollment?.let { loadEnrollmentDetail(it.id) } }
        }
    }

    fun updateEnrollmentStatus(id: Int, estado: String) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            enrollmentRepository.updateEnrollment(id, estado)
                .onSuccess { loadEnrollments() }
                .onFailure { e ->
                    _listState.update { it.copy(error = e.message) }
                }
        }
    }

    fun deleteEnrollment(id: Int) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            enrollmentRepository.deleteEnrollment(id)
                .onSuccess { loadEnrollments() }
                .onFailure { e ->
                    _listState.update { it.copy(error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _listState.update { it.copy(showForm = true, editingEnrollment = null) }
    }

    fun showEditForm(enrollment: Enrollment) {
        _listState.update { it.copy(showForm = true, editingEnrollment = enrollment) }
    }

    fun hideForm() {
        _listState.update { it.copy(showForm = false, editingEnrollment = null) }
    }

    fun clearCreateResult() {
        _createResult.value = null
    }

    fun clearError() {
        _listState.update { it.copy(error = null) }
    }
}
