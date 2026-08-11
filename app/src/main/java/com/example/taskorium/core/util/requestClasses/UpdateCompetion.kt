package com.example.taskorium.core.util.requestClasses

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCompletionRequest(
    val id: String,
    val isCompleted: Boolean
)