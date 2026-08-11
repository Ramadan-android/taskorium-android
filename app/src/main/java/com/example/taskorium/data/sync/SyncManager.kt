package com.example.taskorium.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskorium.data.sync.worker.CategorySyncWorker
import com.example.taskorium.data.sync.worker.TaskSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
//        .setRequiresBatteryNotLow(true)
//    .setRequiresBatteryNotLow(true)
        .build()
    fun scheduleOneTimeSync() {
        val taskSyncRequest = OneTimeWorkRequestBuilder<TaskSyncWorker>()
            .setConstraints(constraints)
            .build()

        val categorySyncRequest = OneTimeWorkRequestBuilder<CategorySyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "TaskSyncWork",
            ExistingWorkPolicy.KEEP,
            taskSyncRequest
        )

        workManager.enqueueUniqueWork(
            "CategorySyncWork",
            ExistingWorkPolicy.KEEP,
            categorySyncRequest
        )
//        workManager.getWorkInfosForUniqueWorkLiveData("TaskSyncWork")
//            .observeForever {
//                Log.d("WM", it.toString())
//            }
//        workManager.getWorkInfosForUniqueWorkLiveData("CategorySyncWork")
//            .observeForever {
//                Log.d("WM", it.toString())
//            }
    }
}