package com.example.taskorium.data.remote

import com.example.taskorium.core.util.Constants
import com.example.taskorium.data.remote.dto.CategoryDto
import com.example.taskorium.data.remote.dto.TaskDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CategoryApiService {
    @GET(Constants.GET_CATEGORY_ENDPOINT)
    suspend fun getCategories(): Response<List<CategoryDto>>
//            Response<CategoryResponse>

    @POST(Constants.ADD_CATEGORY_ENDPOINT)
    suspend fun addCategory(
        @Body category: CategoryDto
    ): Response<Unit>

//    @PUT(Constants.UPDATE_CATEGORY_ENDPOINT)
//    suspend fun updateCategory(
//        @Body category: CategoryDto
//    ): Response<Unit>

    @POST(Constants.ADD_CATEGORY_ENDPOINT)
    suspend fun addCategories(
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body data: List<CategoryDto>
    ): Response<Unit>
//    @PUT(Constants.UPDATE_CATEGORY_ENDPOINT)
//    suspend fun updateCategories(
//        @Body data: List<CategoryDto>
//    ): Response<Unit>
    @DELETE(Constants.DELETE_CATEGORY_ENDPOINT)
    suspend fun deleteCategory(
        @Query("id") categoryId: String
    ): Response<Unit>
}