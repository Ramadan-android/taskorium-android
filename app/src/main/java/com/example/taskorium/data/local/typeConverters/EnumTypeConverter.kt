package com.example.taskorium.data.local.typeConverters

import androidx.room.TypeConverter
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.SyncStatus

class EnumTypeConverter {

    @TypeConverter
    fun priorityToString(priority: Priority): String{
        return priority.name
    }

    @TypeConverter
    fun stringTopriority(value: String): Priority{
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun statusToString(status: SyncStatus): String{
        return status.name
    }

    @TypeConverter
    fun stringToStatus(value: String): SyncStatus{
        return SyncStatus.valueOf(value)
    }
}