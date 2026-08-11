package com.example.taskorium.ui.features.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val authButtonText: String = AuthScreenMode.Login.name,
    val toggleButtonText: String = AuthScreenMode.Register.name,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val screenMode: AuthScreenMode = AuthScreenMode.Login
)

enum class AuthScreenMode{
    Login,
    Register
}
