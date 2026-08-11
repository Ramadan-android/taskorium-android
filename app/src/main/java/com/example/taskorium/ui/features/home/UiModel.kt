package com.example.taskorium.ui.features.home

import com.example.taskorium.domain.model.Task

data class TaskUiModel(
    val task: Task,
    val categoryName: String
)