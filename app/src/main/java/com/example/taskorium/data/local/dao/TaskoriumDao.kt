package com.example.taskorium.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskoriumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(taskList: List<TaskEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    /**
     *
     * default [Constants.DEFAULT_CATEGORY_ID] all (الكل)
     *
     */
    @Query("""
SELECT * FROM ${Constants.TASK_TABLE}
WHERE (:categoryId = '${Constants.DEFAULT_CATEGORY_ID}' OR categoryId = :categoryId)
AND isDeleted = :isDeleted
""")
    fun getTasksByCategoryId(categoryId: String = Constants.DEFAULT_CATEGORY_ID, isDeleted: Boolean = false): Flow<List<TaskEntity>>

    @Query("""
SELECT id FROM ${Constants.TASK_TABLE}
WHERE categoryId = :categoryId
""")
    suspend fun getTasksToDeleteByCategoryId(categoryId: String): List<String>

    @Query("SELECT * FROM ${Constants.TASK_TABLE} WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    @Query("SELECT * FROM ${Constants.TASK_TABLE} WHERE syncStatus = :syncStatus AND isDeleted = 0")
    suspend fun getUnsyncedActiveTasks(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED): List<TaskEntity>

    @Query("SELECT * FROM ${Constants.TASK_TABLE} WHERE syncStatus = :syncStatus AND isDeleted = 1")
    suspend fun getUnsyncedDeletedTasks(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED): List<TaskEntity>

    @Query("DELETE FROM ${Constants.TASK_TABLE} WHERE isDeleted = 1 AND syncStatus = :syncStatus")
    suspend fun deleteSyncedDeletedTasks(syncStatus: SyncStatus = SyncStatus.NOT_SYNCED)
    @Query("UPDATE ${Constants.TASK_TABLE} SET isCompleted = :isCompleted, syncStatus = :status WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean, status: SyncStatus = SyncStatus.NOT_SYNCED)

    @Query("UPDATE ${Constants.TASK_TABLE} SET isDeleted = :isDeleted, syncStatus = :status WHERE id = :taskId")
    suspend fun softDeleteTask(taskId: String, isDeleted: Boolean = true, status: SyncStatus = SyncStatus.NOT_SYNCED)

    @Query("DELETE FROM ${Constants.TASK_TABLE} WHERE id = :taskId")
    suspend fun hardDeleteTask(taskId: String)

    @Query("UPDATE ${Constants.TASK_TABLE} SET isDeleted = :isDeleted, syncStatus = :status WHERE categoryId = :categoryId")
    suspend fun softDeleteTasksByCategory(categoryId: String, isDeleted: Boolean = true, status: SyncStatus = SyncStatus.NOT_SYNCED)

    @Query("DELETE FROM ${Constants.TASK_TABLE} WHERE categoryId = :categoryId")
    suspend fun hardDeleteTasksByCategory(categoryId: String)
    @Query("DELETE FROM ${Constants.TASK_TABLE}")
    suspend fun clearTasks()




}