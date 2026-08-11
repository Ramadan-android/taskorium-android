package com.example.taskorium.domain.repository

import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksByCategoryId(categoryId: String): Flow<List<Task>>
    suspend fun getTaskById(taskId: String): Task?

    suspend fun syncTasks(): Boolean

    suspend fun fetchTasks(): NetworkResult<String>
    suspend fun insertTask(task: Task)

    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean)

    suspend fun deleteTasksByCategory(categoryId: String)
    suspend fun deleteTask(taskId: String)



}