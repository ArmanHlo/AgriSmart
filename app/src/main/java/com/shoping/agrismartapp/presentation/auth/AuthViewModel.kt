package com.shoping.agrismartapp.presentation.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoping.agrismartapp.domain.model.User
import com.shoping.agrismartapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(true)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser
        .onEach {
            _isAuthLoading.value = false
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    init {
        viewModelScope.launch {
            repository.currentUser.first()
            _isAuthLoading.value = false
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(password: String) {
        _state.update { it.copy(confirmPassword = password) }
    }

    fun register() {
        viewModelScope.launch {
            val email = _state.value.email
            val password = _state.value.password
            val confirmPassword = _state.value.confirmPassword

            if (email.isBlank() || password.isBlank()) {
                _state.update { it.copy(error = "Email and Password cannot be empty") }
                return@launch
            }

            if (password != confirmPassword) {
                _state.update { it.copy(error = "Passwords do not match") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }
            repository.signUpWithEmail(email, password)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, isRegistered = true, user = user) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loginWithEmail() {
        viewModelScope.launch {
            if (_state.value.email.isBlank() || _state.value.password.isBlank()) {
                _state.update { it.copy(error = "Email and Password cannot be empty") }
                return@launch
            }
            _state.update { it.copy(isLoading = true, error = null) }
            repository.signInWithEmail(_state.value.email, _state.value.password)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, isVerified = true, user = user) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.update { AuthState() }
        }
    }

    fun saveProfile(name: String, location: String, farmSize: String, primaryCrop: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val uid = repository.currentUid
            if (uid == null) {
                _state.update { it.copy(isLoading = false, error = "Authentication session lost. Please log in again.") }
                return@launch
            }

            val currentUserData = currentUser.value ?: User(uid = uid)

            val updatedUser = currentUserData.copy(
                name = name,
                location = location,
                farmSize = farmSize,
                primaryCrop = primaryCrop
            )

            repository.saveUserProfile(updatedUser)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isProfileSaved = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.uploadProfileImage(uri)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
