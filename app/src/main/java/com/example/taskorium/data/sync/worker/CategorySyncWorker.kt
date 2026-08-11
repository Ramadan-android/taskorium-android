package com.example.taskorium.data.sync.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskorium.domain.repository.CategoryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CategorySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val categoryRepository: CategoryRepository

): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("CategorySyncWorker","do worker")
        return try {
            categoryRepository.syncCategories().let {isSynced->
                if (isSynced) Result.success() else Result.retry()
            }
        }catch (e: Exception){
            Log.d("CategorySyncWorker","catch==> $e")
            Result.retry()
        }
    }
}