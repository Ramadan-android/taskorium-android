package com.example.taskorium.ui.features.addTask

import com.example.taskorium.core.util.Priority
import com.example.taskorium.domain.model.Category

data class AddTaskUiState(
    val categories: List<Category> = emptyList(),
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val categoryId: String = "",
    val taskEditId: String = "",
    val categoryName: String = "",
    val welcomeText: String = "",
    val showCategoryFields: Boolean = false,
    val isLoading: Boolean = false,
    val screenMode: AddEditScreenMode = AddEditScreenMode.ADD

){
    val enabledButton: Boolean get() {
        val isTitleValid = title.isNotBlank()
        return if (showCategoryFields){
            isTitleValid && categoryName.isNotBlank() && !isLoading
        }else{
            isTitleValid && categoryId.isNotBlank() && !isLoading
        }
    }

}
enum class AddEditScreenMode{
    ADD,
    EDIT
}