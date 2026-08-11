package com.example.taskorium.data.repository.mappers

import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.data.local.entity.TaskEntity
import com.example.taskorium.data.remote.dto.TaskDto
import com.example.taskorium.domain.model.Task

fun TaskDto.toEntity(
    isDeleted: Boolean = false,
    syncStatus: SyncStatus = SyncStatus.SYNCED): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
        priority = try {
            Priority.valueOf(this.priority.uppercase())
        } catch (e: Exception) {
            Priority.MEDIUM
        },
        categoryId = this.categoryId,
        isCompleted = this.isCompleted,
        isDeleted = isDeleted,
        syncStatus = syncStatus
    )
}
fun TaskEntity.toDto(): TaskDto {
    return TaskDto(
        id = this.id,
        title = this.title,
        description = this.description ,
        publishedAt = this.publishedAt,
        priority = this.priority.name,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description ,
        publishedAt = this.publishedAt,
        priority = this.priority,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted,
    )
}
fun Task.toEntity(
    syncStatus: SyncStatus = SyncStatus.NOT_SYNCED,
    isDeleted: Boolean = false
): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
        priority = this.priority,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted,
        isDeleted = isDeleted,
        syncStatus = syncStatus
    )
}

fun Task.toDto(): TaskDto{
    return TaskDto(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
        priority = this.priority.name,
        categoryId = this.categoryId,
        isCompleted = this.isCompleted
    )
}