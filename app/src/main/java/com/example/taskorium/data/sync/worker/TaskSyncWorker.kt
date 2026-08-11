package com.example.taskorium.data.sync.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskorium.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            taskRepository.syncTasks().let {isSynced->
                if (isSynced) Result.success() else Result.retry()
            }
        }catch (_: Exception){
            Result.retry()
        }
    }
}
