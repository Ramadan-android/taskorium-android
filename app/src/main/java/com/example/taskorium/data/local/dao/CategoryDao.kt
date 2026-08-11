package com.example.taskorium.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.data.local.entity.CategoryEntity
import com.example.taskorium.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM ${Constants.CATEGORY_TABLE} WHERE isDeleted = 0")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ${Constants.CATEGORY_TABLE} WHERE id = :categoryId")
    suspend fun getCategory(categoryId: String): CategoryEntity
    @Query("SELECT * FROM ${Constants.CATEGORY_TABLE} WHERE syncStatus = :syncStatus AND isDeleted = 0")
    suspend fun getUnsyncedActiveCategories(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED): List<CategoryEntity>

    @Query("SELECT * FROM ${Constants.CATEGORY_TABLE} WHERE syncStatus = :syncStatus AND isDeleted = 1")
    suspend fun getUnsyncedDeletedCategories(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED): List<CategoryEntity>

    @Query("DELETE FROM ${Constants.CATEGORY_TABLE} WHERE isDeleted = 1 AND syncStatus = :syncStatus")
    suspend fun deleteSyncedDeletedCategories(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categoryList: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("UPDATE ${Constants.CATEGORY_TABLE} SET isDeleted = :isDeleted, syncStatus = :status WHERE id = :categoryId")
    suspend fun softDeleteCategory(categoryId: String, isDeleted: Boolean = true, status: SyncStatus = SyncStatus.NOT_SYNCED)

    @Query("DELETE FROM ${Constants.CATEGORY_TABLE} WHERE id = :categoryId")
    suspend fun hardDeleteCategory(categoryId: String)

    @Query("DELETE FROM ${Constants.CATEGORY_TABLE}")
    suspend fun clearCategories()
}