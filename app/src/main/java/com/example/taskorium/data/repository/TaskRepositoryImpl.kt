package com.example.taskorium.data.repository

import android.util.Log
import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.core.util.requestClasses.UpdateCompletionRequest
import com.example.taskorium.data.local.dao.TaskoriumDao
import com.example.taskorium.data.local.entity.TaskEntity
import com.example.taskorium.data.remote.TaskoriumApiService
import com.example.taskorium.data.repository.mappers.toDomain
import com.example.taskorium.data.repository.mappers.toDto
import com.example.taskorium.data.repository.mappers.toEntity
import com.example.taskorium.domain.model.Task
import com.example.taskorium.domain.repository.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject
import kotlin.collections.joinToString
import kotlin.collections.map

class TaskRepositoryImpl @Inject constructor(
    private val taskDB: TaskoriumDao,
    private val taskApi: TaskoriumApiService,
): TaskRepository {
    override fun getTasksByCategoryId(categoryId: String): Flow<List<Task>> =
         taskDB.getTasksByCategoryId(categoryId = categoryId)
            .map {entities: List<TaskEntity> ->
                entities.map {
                    it.toDomain()
                }
            }.flowOn(Dispatchers.IO)

    override suspend fun getTaskById(taskId: String): Task? = withContext(Dispatchers.IO){
        taskDB.getTaskById(taskId)?.toDomain()
    }

    override suspend fun syncTasks(): Boolean = withContext(Dispatchers.IO)
    {
        val unsyncedTasks = taskDB.getUnsyncedActiveTasks(syncStatus = SyncStatus.NOT_SYNCED)
        val unsyncedDeletedTasks = taskDB.getUnsyncedDeletedTasks()
        val unsyncedTasksResult = unsyncedTasks(unsyncedTasks)
        val unsyncedDeletedTasksResult = unsyncedDeletedDtoTasks(unsyncedDeletedTasks)
        return@withContext unsyncedTasksResult && unsyncedDeletedTasksResult
    }

    private suspend fun unsyncedTasks(unsyncedTasksList: List<TaskEntity>): Boolean = withContext(
        Dispatchers.IO){
        if (unsyncedTasksList.isEmpty())return@withContext true

        val isSynced = try {
            val unsyncedDtoTasks = unsyncedTasksList
                .map { it.toDto() }
            val response = taskApi.addTasks(unsyncedDtoTasks)
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) {
            val syncedTasks = unsyncedTasksList.map {
                it.copy(syncStatus = SyncStatus.SYNCED)
            }
            taskDB.insertTasks(syncedTasks)
            true
        }else{
            false
        }
    }

    private suspend fun unsyncedDeletedDtoTasks(unsyncedTasksList:  List<TaskEntity>): Boolean = withContext(
        Dispatchers.IO){
        if (unsyncedTasksList.isEmpty()) return@withContext true

        val isSynced = try {
            val unsyncedDeletedDtoTasks = unsyncedTasksList
                .map { it.toDto() }
            val ids = unsyncedDeletedDtoTasks.map { it.id }
            val response = taskApi.deleteTask("in.(${ids.joinToString(",")})")
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) {
            taskDB.deleteSyncedDeletedTasks()
            true
        }else{
            false
        }
    }

    override suspend fun fetchTasks(): NetworkResult<String> = withContext(Dispatchers.IO)
    {
        try {
            val response = taskApi.getTasks()
            if (response.isSuccessful){
                val dtoTasks = response.body()
                return@withContext if (dtoTasks != null){
                    val entityTasks = dtoTasks.map { it.toEntity(isDeleted = false, syncStatus = SyncStatus.SYNCED) }
                    taskDB.insertTasks(entityTasks)
                    NetworkResult.Success("data fetched")
                }else{
                    NetworkResult.Error("no have tasks, add one")
                }
            }else{
                return@withContext when (response.code()) {
                    401 -> NetworkResult.Error("ops have a problem")
                    400 -> NetworkResult.Error("ops have a problem")
                    else -> NetworkResult.Error("حدث خطأ في السيرفر: ${response.message()}")
                }
            }
        }catch (_: IOException){
            return@withContext NetworkResult.Error("فشل الاتصال، يرجى التحقق من شبكة الإنترنت")
        }catch (e: Exception){
            if (e is CancellationException) throw e
            return@withContext NetworkResult.Error("حدث خطأ غير متوقع: ${e.localizedMessage}")
        }
    }

    override suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        val localTask = task.toEntity(syncStatus = SyncStatus.NOT_SYNCED)
        taskDB.insertTask(localTask)
        val isSynced = try {
            val response = taskApi.addTask(task.toDto())
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) taskDB.insertTask(localTask.copy(syncStatus = SyncStatus.SYNCED))
    }

    override suspend fun toggleTaskCompletion(
        taskId: String,
        isCompleted: Boolean
    ) = withContext(Dispatchers.IO) {
        taskDB.updateTaskCompletion(
            taskId = taskId,
            isCompleted = isCompleted,
            status = SyncStatus.NOT_SYNCED
        )
        val isSynced = try {
            val response = taskApi.updateTaskCompletion(
                UpdateCompletionRequest(
                    id = taskId,
                    isCompleted = isCompleted,
                )
            )
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) taskDB.updateTaskCompletion(
            taskId = taskId,
            isCompleted = isCompleted,
            status = SyncStatus.SYNCED
        )
    }

    override suspend fun deleteTasksByCategory(categoryId: String) = withContext(Dispatchers.IO) {
        taskDB.softDeleteTasksByCategory(
            categoryId = categoryId,
            isDeleted = true,
            status = SyncStatus.NOT_SYNCED
        )
        val ids = taskDB.getTasksToDeleteByCategoryId(categoryId)
        val isSynced = try {
            if (ids.isNotEmpty()){
                val response = taskApi.deleteTask("in.(${ids.joinToString(",")})")
                response.isSuccessful
            }else false
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) taskDB.hardDeleteTasksByCategory(categoryId)
    }

    override suspend fun deleteTask(
        taskId: String,
    ) = withContext(Dispatchers.IO) {
        taskDB.softDeleteTask(
            taskId = taskId,
            isDeleted = true,
            status = SyncStatus.NOT_SYNCED
        )
        val isSynced = try {
            val response = taskApi.deleteTask(taskId = "eq.$taskId")
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) taskDB.hardDeleteTask(taskId)
    }

}