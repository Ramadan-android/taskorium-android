package com.example.taskorium.ui.features.editDeleteCat


sealed interface EditDeleteCatUiEvent {
    data class CategoryChanged(val value: String): EditDeleteCatUiEvent
    data object ClickSaveButton: EditDeleteCatUiEvent
    data class ClickDeleteButton(val categoryId: String): EditDeleteCatUiEvent

}

sealed interface EditDeleteCatUiEventEffect {
    data object NavigateToHome: EditDeleteCatUiEventEffect
}