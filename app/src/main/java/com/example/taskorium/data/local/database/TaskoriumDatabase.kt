package com.example.taskorium.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskorium.data.local.dao.CategoryDao
import com.example.taskorium.data.local.dao.TaskoriumDao
import com.example.taskorium.data.local.entity.CategoryEntity
import com.example.taskorium.data.local.entity.TaskEntity
import com.example.taskorium.data.local.typeConverters.EnumTypeConverter

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(EnumTypeConverter::class)
abstract class TaskoriumDatabase: RoomDatabase(){

    abstract fun taskoriumDao(): TaskoriumDao

    abstract fun categoryDao(): CategoryDao
}