package com.example.taskorium.ui.features.addTask

import com.example.taskorium.core.util.Priority
import com.example.taskorium.ui.features.home.HomeUiEffectEvent

sealed interface AddTaskUiEvent {
    data class TitleChanged(val value: String): AddTaskUiEvent
    data class DescriptionChanged(val value: String): AddTaskUiEvent
    data class CategoryChangedName(val value: String): AddTaskUiEvent
    data class ChangePriority(val priority: Priority): AddTaskUiEvent
    data class ChangeCategory(val categoryId: String): AddTaskUiEvent
    data object ClickAddButton: AddTaskUiEvent
    data object ToggleCategoryMode: AddTaskUiEvent
}

sealed interface AddUiEffectEvent {
    data object NavigateToHome: AddUiEffectEvent

}