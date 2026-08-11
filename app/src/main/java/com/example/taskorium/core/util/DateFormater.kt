package com.example.taskorium.core.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toRelativeTimeString(): String {

    val inst = Instant.now().toEpochMilli()
    val diffMillis = inst - this

//    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
//    EEEE, dd MMM yyyy  dd/MM/yyyy HH:mm a

    if (diffMillis <= 0) return "الآن"

    val duration = Duration.ofMillis(diffMillis)
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        // أقل من دقيقة
        minutes < 1 -> "الآن"

        // الدقائق (أقل من ساعة)
        minutes < 60 -> when (minutes) {
            1L -> "منذ دقيقة"
            2L -> "منذ دقيقتين"
            in 3L..10L -> "منذ $minutes دقائق"
            30L -> "منذ نصف ساعة"
            else -> "منذ $minutes دقيقة"
        }

        // الساعات (أقل من يوم)
        hours < 24 -> when (hours) {
            1L -> "منذ ساعة"
            2L -> "منذ ساعتين"
            in 3L..10L -> "منذ $hours ساعات"
            else -> "منذ $hours ساعة"
        }

        // الأيام
        days == 1L -> "أمس"
        days == 2L -> "منذ يومين"
        days in 3L..7L -> "منذ $days أيام"

        // أقدم من أسبوع: عرض التاريخ كاملاً
        else -> {
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
            Instant.ofEpochMilli(this)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        }
    }
}

fun Long.toCalculateDueDate(plusDays: Int): Long {
    val day = 60_000
        //86_400_000
    return this.plus(day * plusDays)
}