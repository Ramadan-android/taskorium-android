package com.example.taskorium.core.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.taskorium.MainActivity
import com.example.taskorium.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.net.toUri

class NotificationHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 1. إظهار الإشعار للمستخدم
    fun showNotification(
        taskId: String,
        title: String,
        content: String,
        priority: String
    ) {
        // تحديد ID القناة المناسبة بناءً على الأولوية
        val channelId = getChannelIdForPriority(priority)

        // التأكد من تسجيل القنوات في النظام أولاً (مطلوب لأندرويد 8.0+)
        createNotificationChannels()

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TASK_ID", taskId) // يمكن استقباله في الـ Activity للانتقال لشاشة المهمة مباشرة
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // بناء كائن الإشعار
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // يمكن استبدالها بأيقونة تطبيقك
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // يختفي الإشعار تلقائياً عند الضغط عليه
            .build()

        // إطلاق الإشعار فوراً للمستخدم باستخدام hashCode الخاص بـ ID المهمة
        notificationManager.notify(taskId.hashCode(), notification)
    }

    // 2. إنشاء القنوات الثلاث المخصصة للأولويات في أندرويد
    private fun createNotificationChannels() {

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .build()

        val highSoundUri = "android.resource://${context.packageName}/${R.raw.task_reminder}".toUri()
//        val soundUri = Uri.Builder()
//            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//            .authority(context.packageName)
//            .appendPath(R.raw.task_reminder.toString())
//            .build()


//        val highSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val highChannel = NotificationChannel(
            HIGH_CHANNEL_ID,
            "إشعارات أولوية عالية",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "إشعارات منبثقة مع صوت للتنبيهات العاجلة"
            setSound(highSoundUri, audioAttributes)
            enableVibration(true)
        }

        val mediumSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mediumChannel = NotificationChannel(
            MEDIUM_CHANNEL_ID,
            "إشعارات أولوية متوسطة",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(mediumSoundUri, audioAttributes)
        }

        val lowChannel = NotificationChannel(
            LOW_CHANNEL_ID,
            "إشعارات أولوية منخفضة",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setSound(null, null)
        }

        notificationManager.createNotificationChannels(listOf(highChannel, mediumChannel, lowChannel))
    }

    // 3. تحديد الـ Channel ID بناءً على الأولوية
    private fun getChannelIdForPriority(priority: String): String {
        return when (priority.uppercase()) {
            "HIGH" -> HIGH_CHANNEL_ID
            "LOW" -> LOW_CHANNEL_ID
            else -> MEDIUM_CHANNEL_ID
        }
    }

    companion object {
        private const val HIGH_CHANNEL_ID = "high_priority_tasks_channel"
        private const val MEDIUM_CHANNEL_ID = "medium_priority_tasks_channel"
        private const val LOW_CHANNEL_ID = "low_priority_tasks_channel"
    }
}