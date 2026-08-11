package com.example.taskorium.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("publishedAt") val publishedAt: Long,
    @SerialName("priority") val priority: String,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("isCompleted") val isCompleted: Boolean,

)
