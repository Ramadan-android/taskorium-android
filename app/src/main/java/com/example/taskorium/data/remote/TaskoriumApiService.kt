package com.example.taskorium.data.remote

import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.requestClasses.UpdateCompletionRequest
import com.example.taskorium.data.remote.dto.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface TaskoriumApiService {

    @GET(Constants.GET_TASK_ENDPOINT)
    suspend fun getTasks(): Response<List<TaskDto>>


    @POST(Constants.ADD_TASK_ENDPOINT)
    suspend fun addTask(
        @Body data: TaskDto
    ): Response<Unit>

    @POST(Constants.ADD_TASK_ENDPOINT)
    suspend fun addTasks(
        @Body data: List<TaskDto>
    ): Response<Unit>

    @PUT(Constants.UPDATE_COMPETITION_TASK_ENDPOINT)
    suspend fun updateTaskCompletion(
        @Body request: UpdateCompletionRequest
    ): Response<Unit>
    @DELETE(Constants.DELETE_TASK_ENDPOINT)
    suspend fun deleteTask(
        @Query("id") taskId: String
    ): Response<Unit>


    @DELETE(Constants.DELETE_TASK_ENDPOINT)
    suspend fun deleteTasksByCategory(
        @Query("id") ids: String
    ): Response<Unit>


}