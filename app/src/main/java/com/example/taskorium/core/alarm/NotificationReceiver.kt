package com.example.taskorium.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver: BroadcastReceiver(){
    @Inject
    lateinit var notificationHelper: NotificationHelper
    override fun onReceive(context: Context?, intent: Intent?) {

        val taskId = intent?.getStringExtra("EXTRA_TASK_ID") ?: return
        val title = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "تذكير بمهمة"
        val priority = intent.getStringExtra("EXTRA_TASK_PRIORITY") ?: "MEDIUM"
        val content = intent.getStringExtra("EXTRA_TASK_DESCRIPTION") ?: "task"

        notificationHelper.showNotification(
            taskId = taskId,
            title = title,
            priority = priority,
            content = content
        )
    }


}