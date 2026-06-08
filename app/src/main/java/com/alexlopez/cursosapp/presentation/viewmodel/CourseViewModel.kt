package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.domain.model.CourseFilters
import com.alexlopez.cursosapp.domain.model.CoursePayload
import com.alexlopez.cursosapp.domain.repository.CategoryRepository
import com.alexlopez.cursosapp.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseUiState(
    val courses: List<Course> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = false,
    val searchQuery: String = "",
    val selectedNivel: String? = null,
    val showForm: Boolean = false,
    val editingCourse: Course? = null,
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow<Course?>(null)
    val detailState: StateFlow<Course?> = _detailState.asStateFlow()

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading.asStateFlow()

    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError.asStateFlow()

    init {
        loadCourses()
        loadCategories()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
            val filters = CourseFilters(
                search = _uiState.value.searchQuery.ifBlank { null },
                nivel = _uiState.value.selectedNivel,
                page = 1,
            )
            courseRepository.getCourses(filters)
                .onSuccess { (courses, count) ->
                    _uiState.update {
                        it.copy(
                            courses = courses,
                            totalCount = count,
                            isLoading = false,
                            hasMore = courses.size < count,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        viewModelScope.launch {
            val nextPage = _uiState.value.currentPage + 1
            _uiState.update { it.copy(isLoadingMore = true) }
            val filters = CourseFilters(
                search = _uiState.value.searchQuery.ifBlank { null },
                nivel = _uiState.value.selectedNivel,
                page = nextPage,
            )
            courseRepository.getCourses(filters)
                .onSuccess { (courses, count) ->
                    _uiState.update {
                        it.copy(
                            courses = it.courses + courses,
                            currentPage = nextPage,
                            isLoadingMore = false,
                            hasMore = it.courses.size + courses.size < count,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
        }
    }

    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadCourses()
    }

    fun filterByNivel(nivel: String?) {
        _uiState.update { it.copy(selectedNivel = nivel) }
        loadCourses()
    }

    fun loadCourse(id: Int) {
        viewModelScope.launch {
            _detailLoading.value = true
            _detailError.value = null
            courseRepository.getCourse(id)
                .onSuccess { course ->
                    _detailState.value = course
                    _detailLoading.value = false
                }
                .onFailure { e ->
                    _detailError.value = e.message
                    _detailLoading.value = false
                }
        }
    }

    fun createCourse(payload: CoursePayload, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            courseRepository.createCourse(payload)
                .onSuccess {
                    loadCourses()
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun updateCourse(id: Int, payload: CoursePayload, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            courseRepository.updateCourse(id, payload)
                .onSuccess {
                    loadCourses()
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(showForm = true, editingCourse = null) }
    }

    fun showEditForm(course: Course) {
        _uiState.update { it.copy(showForm = true, editingCourse = course) }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingCourse = null) }
    }

    fun deleteCourse(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            courseRepository.deleteCourse(id)
                .onSuccess { loadCourses() }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
