package com.example.taskorium.data.repository

import android.util.Log
import  com.example.taskorium.core.util.NetworkResult
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.data.local.dao.CategoryDao
import com.example.taskorium.data.local.entity.CategoryEntity
import com.example.taskorium.data.remote.CategoryApiService
import com.example.taskorium.data.repository.mappers.toDomain
import com.example.taskorium.data.repository.mappers.toDto
import com.example.taskorium.data.repository.mappers.toEntity
import com.example.taskorium.domain.model.Category
import com.example.taskorium.domain.repository.CategoryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val catDB: CategoryDao,
    private val catApi: CategoryApiService
): CategoryRepository{
    override fun getCategories(): Flow<List<Category>> =
        catDB.getCategories().map { entities: List<CategoryEntity> ->
            entities.map {
                it.toDomain()
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getCategory(categoryId : String): Category = withContext(Dispatchers.IO){
        catDB.getCategory(categoryId).toDomain()
    }

    override suspend fun syncCategories(): Boolean {
        val unsyncedCategories = catDB.getUnsyncedActiveCategories(syncStatus = SyncStatus.NOT_SYNCED)
        val unsyncedDeletedCategories = catDB.getUnsyncedDeletedCategories()
        val unsyncedCategoriesResult = unsyncedCategories(unsyncedCategories)
        val unsyncedDeletedCategoriesResult = unsyncedDeletedDtoCategories(unsyncedDeletedCategories)
        return unsyncedCategoriesResult && unsyncedDeletedCategoriesResult
    }

    private suspend fun unsyncedCategories(unsyncedCategoriesList: List<CategoryEntity>): Boolean = withContext(
        Dispatchers.IO){
        if (unsyncedCategoriesList.isEmpty()){
            return@withContext true
        }
        val isSynced = try {
            val unsyncedDtoCategoriesList = unsyncedCategoriesList.map { it.toDto() }
            val response = catApi.addCategories(data = unsyncedDtoCategoriesList)
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) {
            val syncedCategories =
                unsyncedCategoriesList.map {
                    it.copy(syncStatus = SyncStatus.SYNCED)
                }
            catDB.insertCategories(syncedCategories)
            true
        }else false
    }

    private suspend fun unsyncedDeletedDtoCategories(unsyncedCategoriesList:  List<CategoryEntity>): Boolean = withContext(
        Dispatchers.IO) {
        if (unsyncedCategoriesList.isEmpty()) return@withContext true
        val isSynced = try {
            val unsyncedDtoCategoriesList = unsyncedCategoriesList.map { it.toDto() }
            val ids = unsyncedDtoCategoriesList.map { it.id }
            val response = catApi.deleteCategory("in.(${ids.joinToString(",")})")
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) {
            catDB.deleteSyncedDeletedCategories()
            true
        }else{
            false
        }

    }
    override suspend fun fetchCategories(): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = catApi.getCategories()
            if (response.isSuccessful){
                val dtoList = response.body()
                return@withContext if (dtoList != null){
                    val entityCategories = dtoList
                        .map { it.toEntity().copy(isDeleted = false, syncStatus = SyncStatus.SYNCED) }
                    catDB.insertCategories(entityCategories)
                    NetworkResult.Success("data fetched")
                }else{
                    NetworkResult.Error("no have categories, add one")
                }
            }else{
                return@withContext when (response.code()) {
                    401 -> NetworkResult.Error("ops have a problem")
                    400 -> NetworkResult.Error("ops have a problem")
                    else -> NetworkResult.Error("حدث خطأ في السيرفر: ${response.message()}")
                }
            }
        } catch (_: IOException){
            return@withContext NetworkResult.Error("فشل الاتصال، يرجى التحقق من شبكة الإنترنت")
        }catch (e: Exception){
            if (e is CancellationException) throw e
            return@withContext NetworkResult.Error("حدث خطأ غير متوقع: ${e.localizedMessage}")
        }
    }

    override suspend fun insertCategory(category: Category) = withContext(Dispatchers.IO) {
        val localEntity = category.toEntity(syncStatus = SyncStatus.NOT_SYNCED)
        catDB.insertCategory(localEntity)
        val isSynced = try {
            val response = catApi.addCategory(category.toDto())
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            false
        }
        if (isSynced) catDB.insertCategory(localEntity.copy(syncStatus = SyncStatus.SYNCED))

    }

    override suspend fun deleteCategory(
        categoryId: String
    ) = withContext(Dispatchers.IO) {
        Log.d("test lag", "im in deleteCategory top")

            catDB.softDeleteCategory(
            categoryId = categoryId,
            isDeleted = true,
            status = SyncStatus.NOT_SYNCED
        )
        val isSynced = try {
            val response = catApi.deleteCategory(categoryId = "eq.$categoryId")
            response.isSuccessful
        }catch (e: Exception){
            if (e is CancellationException) throw e
            Log.d("test lag", "im in deleteCategory catch")

            false
        }
        if (isSynced) {
            Log.d("test lag", "im in deleteCategory done")

            catDB.hardDeleteCategory(categoryId)
        }

    }




}