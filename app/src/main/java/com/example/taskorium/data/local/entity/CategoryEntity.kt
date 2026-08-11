package com.example.taskorium.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.SyncStatus

@Entity(tableName = Constants.CATEGORY_TABLE)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isDeleted: Boolean,
    val syncStatus: SyncStatus

)