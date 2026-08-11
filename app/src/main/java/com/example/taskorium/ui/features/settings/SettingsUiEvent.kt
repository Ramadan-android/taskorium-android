package com.example.taskorium.ui.features.settings

sealed interface SettingsUiEvent {
    data object ClickLogOutButton: SettingsUiEvent
}