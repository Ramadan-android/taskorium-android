package com.example.taskorium.ui.features.editDeleteCat

data class EditDeleteCatState(
    val catValue: String = "",
    val categoryId: String = "",
    val oldCatValue: String = "",
    val isLoading: Boolean = false,
){
    val enabledButton: Boolean get() {
        return catValue.isNotBlank() && (catValue != oldCatValue) && !isLoading
    }
}
