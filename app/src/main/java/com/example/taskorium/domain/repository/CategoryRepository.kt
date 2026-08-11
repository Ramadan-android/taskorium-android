package com.example.taskorium.domain.repository

import com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategories(): Flow<List<Category>>

    suspend fun getCategory(categoryId : String): Category

    suspend fun syncCategories(): Boolean

    suspend fun fetchCategories(): NetworkResult<String>

    suspend fun insertCategory(category: Category)

    suspend fun deleteCategory(categoryId: String)


}