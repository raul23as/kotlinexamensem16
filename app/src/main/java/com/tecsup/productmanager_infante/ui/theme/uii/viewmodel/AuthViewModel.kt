package com.tecsup.productmanager_infante.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productmanager_infante.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _isLoggedIn.value = repository.getCurrentUser() != null
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Todos los campos son obligatorios")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)

            _authState.value = if (result.isSuccess) {
                _isLoggedIn.value = true
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _authState.value = AuthState.Error("Todos los campos son obligatorios")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password)

            _authState.value = if (result.isSuccess) {
                _isLoggedIn.value = true
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al registrarse")
            }
        }
    }

    fun logout() {
        repository.logout()
        _isLoggedIn.value = false
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}