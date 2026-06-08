package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.CategoryPayload
import com.alexlopez.cursosapp.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingCategory: Category? = null,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init { loadCategories() }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(showForm = true, editingCategory = null) }
    }

    fun showEditForm(category: Category) {
        _uiState.update { it.copy(showForm = true, editingCategory = category) }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingCategory = null) }
    }

    fun createCategory(payload: CategoryPayload) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            categoryRepository.createCategory(payload)
                .onSuccess {
                    loadCategories()
                    hideForm()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun updateCategory(id: Int, payload: CategoryPayload) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            categoryRepository.updateCategory(id, payload)
                .onSuccess {
                    loadCategories()
                    hideForm()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            categoryRepository.deleteCategory(id)
                .onSuccess { loadCategories() }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
