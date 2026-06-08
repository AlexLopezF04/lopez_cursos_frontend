package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.model.ProgressPayload
import com.alexlopez.cursosapp.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val progressList: List<Progress> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingProgress: Progress? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = false,
    val deleteConfirmId: Int? = null,
    val loadedOnce: Boolean = false,
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun loadProgress(matriculaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
            progressRepository.getProgressList(matriculaId)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(progressList = list, isLoading = false, hasMore = false, loadedOnce = true)
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message, loadedOnce = true) }
                }
        }
    }

    fun loadAllProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1) }
            progressRepository.getProgressList(null)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(progressList = list, isLoading = false, hasMore = false)
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun createProgress(payload: ProgressPayload) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            progressRepository.createProgress(payload)
                .onSuccess { loadAllProgress() }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteProgress(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            progressRepository.deleteProgress(id)
                .onSuccess { loadAllProgress() }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun toggleComplete(id: Int, completada: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            progressRepository.updateProgress(id, completada)
                .onSuccess {
                    val updated = _uiState.value.progressList.map {
                        if (it.id == id) it.copy(completada = completada) else it
                    }
                    _uiState.update { it.copy(progressList = updated, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun autoMarkComplete(matriculaId: Int, leccionId: Int) {
        viewModelScope.launch {
            val existing = _uiState.value.progressList.find { it.leccionId == leccionId }
            if (existing != null) {
                if (!existing.completada) {
                    progressRepository.updateProgress(existing.id, true)
                        .onSuccess {
                            _uiState.update { state ->
                                val updated = state.progressList.map {
                                    if (it.id == existing.id) it.copy(completada = true) else it
                                }
                                state.copy(progressList = updated)
                            }
                        }
                }
            } else {
                progressRepository.createProgress(
                    ProgressPayload(matriculaId, leccionId, completada = true)
                ).onSuccess { created ->
                    _uiState.update { state ->
                        state.copy(progressList = state.progressList + created)
                    }
                }
            }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(showForm = true, editingProgress = null) }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingProgress = null) }
    }

    fun showDeleteConfirm(id: Int) {
        _uiState.update { it.copy(deleteConfirmId = id) }
    }

    fun hideDeleteConfirm() {
        _uiState.update { it.copy(deleteConfirmId = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
