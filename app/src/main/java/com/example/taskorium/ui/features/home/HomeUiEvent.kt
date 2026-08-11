package com.example.taskorium.ui.features.home


sealed interface HomeUiEvent{

    data class CategoryChange(val catId: String): HomeUiEvent
    data class SearchQueryChange(val value: String): HomeUiEvent
    data class ToggleTaskCompletion(val taskId: String, val isCompleted: Boolean): HomeUiEvent
    data class DeleteTask(val taskId: String, val catId: String): HomeUiEvent
}
sealed interface HomeUiEffectEvent{
    data class NavigateToAddEdit(val taskId: String): HomeUiEffectEvent
    data class NavigateToCat(val catId: String): HomeUiEffectEvent
    data object NavigateToSettings: HomeUiEffectEvent
}