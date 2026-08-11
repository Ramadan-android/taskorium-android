package com.example.taskorium.ui.features.home

import com.example.taskorium.core.util.Constants
import com.example.taskorium.domain.model.Category

/**
 * [tasks] and [categories] are not needed because we well used statIn and save them in val after then collect as state
 * [searchQuery] and [filterBy] we well used in one case the ui have a search text filed and filters
 */
data class HomeUiState(
    val tasksUi: List<TaskUiModel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = Constants.DEFAULT_CATEGORY_ID,
    val searchQuery: String = "",
    val filterBy: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
