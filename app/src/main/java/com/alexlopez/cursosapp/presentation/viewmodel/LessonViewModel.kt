package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.LessonPayload
import com.alexlopez.cursosapp.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lessons: List<Lesson> = emptyList(),
    val currentLesson: Lesson? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingLesson: Lesson? = null,
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLessons(cursoId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lessonRepository.getLessons(cursoId)
                .onSuccess { lessons ->
                    _uiState.update { it.copy(lessons = lessons, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadLesson(cursoId: Int, lessonId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lessonRepository.getLesson(cursoId, lessonId)
                .onSuccess { lesson ->
                    _uiState.update { it.copy(currentLesson = lesson, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun createLesson(cursoId: Int, payload: LessonPayload, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lessonRepository.createLesson(cursoId, payload)
                .onSuccess {
                    loadLessons(cursoId)
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateLesson(cursoId: Int, lessonId: Int, payload: LessonPayload, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lessonRepository.updateLesson(cursoId, lessonId, payload)
                .onSuccess {
                    loadLessons(cursoId)
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteLesson(cursoId: Int, lessonId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            lessonRepository.deleteLesson(cursoId, lessonId)
                .onSuccess { loadLessons(cursoId) }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(showForm = true, editingLesson = null) }
    }

    fun showEditForm(lesson: Lesson) {
        _uiState.update { it.copy(showForm = true, editingLesson = lesson) }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingLesson = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
