package com.example.taskorium.domain.model

import com.example.taskorium.core.util.Priority

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val publishedAt: Long,
    val dueDate: Long? = null,
    val priority: Priority,
    val categoryId: String,
    val isCompleted: Boolean,
)
