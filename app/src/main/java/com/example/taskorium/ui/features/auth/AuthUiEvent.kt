package com.example.taskorium.ui.features.auth

sealed class AuthUiEvent {
    data class EmailChanged(val value: String): AuthUiEvent()
    data class PasswordChanged(val value: String) : AuthUiEvent()
    data object ToggleScreenMode: AuthUiEvent()
    data object LoginClicked: AuthUiEvent()
    data object RegisterClicked: AuthUiEvent()
}

sealed interface AuthUiEffectEvent{
    data object NavigateToHome: AuthUiEffectEvent
}