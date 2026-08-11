package com.example.taskorium.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.domain.repository.AuthRepository
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val catRepository: CategoryRepository
): ViewModel(){
    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()
    private val _effectEvent = MutableSharedFlow<AuthUiEffectEvent>()
    val effectEvent = _effectEvent.asSharedFlow()
    fun onEvent(event: AuthUiEvent){
        when(event){
            is AuthUiEvent.EmailChanged -> {
                _state.update {
                    it.copy(email = event.value)
                }
            }
            is AuthUiEvent.PasswordChanged -> {
                _state.update {
                    it.copy(password = event.value)
                }
            }
            AuthUiEvent.ToggleScreenMode -> {
                val screenModeStateIsLogin = _state.value.screenMode == AuthScreenMode.Login
                val updatedScreenMode = if (screenModeStateIsLogin) AuthScreenMode.Register else AuthScreenMode.Login
                _state.update {
                    it.copy(
                        screenMode = updatedScreenMode,
                        authButtonText = updatedScreenMode.name,
                        toggleButtonText = _state.value.screenMode.name
                    )
                }
            }
            AuthUiEvent.LoginClicked -> {
                login()
            }
            AuthUiEvent.RegisterClicked -> {
                register()
            }

        }
    }

    private fun login() {
        _state.update { it.copy(isLoading = true) }
        val email = _state.value.email
        val password = _state.value.password
        viewModelScope.launch {
            when(val loginResult = authRepository.login(email, password)){
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = loginResult.message
                        )
                    }
                }
                NetworkResult.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
                is NetworkResult.Success<*> -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadApiData()
                    NavigateToHome()

                }
            }
        }
    }

    private fun register() {
        _state.update { it.copy(isLoading = true) }
        val email = _state.value.email
        val password = _state.value.password
        viewModelScope.launch {
            when(val registerResult = authRepository.register(email, password)){
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = registerResult.message
                        )
                    }
                }
                NetworkResult.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }
                is NetworkResult.Success<*> -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadApiData()
                    NavigateToHome()
                }
            }
        }

    }

    private fun loadApiData(){
        viewModelScope.launch {
            taskRepository.fetchTasks()
            catRepository.fetchCategories()
        }
    }

    fun NavigateToHome(){
        viewModelScope.launch {
            _effectEvent.emit(AuthUiEffectEvent.NavigateToHome)
        }
    }
}