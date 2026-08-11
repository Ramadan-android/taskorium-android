package com.example.taskorium.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.SyncStatus

@Entity(tableName = Constants.TASK_TABLE)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val publishedAt: Long,
    val priority: Priority,
    val categoryId: String,
    val isCompleted: Boolean,
    val isDeleted: Boolean,
    val syncStatus: SyncStatus
)
