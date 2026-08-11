package com.example.taskorium.data.repository.mappers

import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.data.local.entity.CategoryEntity
import com.example.taskorium.data.remote.dto.CategoryDto
import com.example.taskorium.domain.model.Category


fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        isDeleted = false,
        syncStatus = SyncStatus.SYNCED
    )
}
fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        id = this.id,
        name = this.name
    )
}

fun Category.toDto(): CategoryDto{
    return CategoryDto(
        id = this.id,
        name = this.name
    )
}
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
    )
}
fun Category.toEntity(
    syncStatus: SyncStatus = SyncStatus.NOT_SYNCED,
    isDeleted: Boolean = false
): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        isDeleted = isDeleted,
        syncStatus = syncStatus
    )
}

