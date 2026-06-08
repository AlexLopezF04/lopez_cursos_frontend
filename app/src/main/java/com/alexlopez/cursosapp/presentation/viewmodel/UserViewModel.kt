package com.alexlopez.cursosapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexlopez.cursosapp.domain.model.User
import com.alexlopez.cursosapp.domain.model.UserPayload
import com.alexlopez.cursosapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showForm: Boolean = false,
    val editingUser: User? = null,
)

data class UserProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _listState = MutableStateFlow(UserListState())
    val listState: StateFlow<UserListState> = _listState.asStateFlow()

    private val _profileState = MutableStateFlow(UserProfileState())
    val profileState: StateFlow<UserProfileState> = _profileState.asStateFlow()

    init { loadUsers() }

    fun loadUsers(search: String? = null) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            userRepository.getUsers(search = search)
                .onSuccess { (users, _) ->
                    _listState.update { it.copy(users = users, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null) }
            userRepository.getMyProfile()
                .onSuccess { user ->
                    _profileState.update { it.copy(user = user, isLoading = false) }
                }
                .onFailure { e ->
                    _profileState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateProfile(payload: UserPayload) {
        viewModelScope.launch {
            _profileState.update { it.copy(isSaving = true) }
            userRepository.updateMyProfile(payload)
                .onSuccess { user ->
                    _profileState.update { it.copy(user = user, isSaving = false) }
                }
                .onFailure { e ->
                    _profileState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun showCreateForm() {
        _listState.update { it.copy(showForm = true, editingUser = null) }
    }

    fun showEditForm(user: User) {
        _listState.update { it.copy(showForm = true, editingUser = user) }
    }

    fun hideForm() {
        _listState.update { it.copy(showForm = false, editingUser = null) }
    }

    fun clearError() {
        _listState.update { it.copy(error = null) }
    }

    fun createUser(payload: UserPayload) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            userRepository.createUser(payload)
                .onSuccess {
                    loadUsers()
                    hideForm()
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateUser(id: Int, payload: UserPayload) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            userRepository.updateUser(id, payload)
                .onSuccess {
                    loadUsers()
                    hideForm()
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            userRepository.deleteUser(id)
                .onSuccess { loadUsers() }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
