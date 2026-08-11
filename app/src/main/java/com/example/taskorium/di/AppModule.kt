package com.example.taskorium.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.taskorium.core.alarm.AndroidAlarmScheduler
import com.example.taskorium.core.util.Constants
import com.example.taskorium.data.local.dao.CategoryDao
import com.example.taskorium.data.local.dao.TaskoriumDao
import com.example.taskorium.data.local.database.TaskoriumDatabase
import com.example.taskorium.data.repository.AuthRepositoryImpl
import com.example.taskorium.data.repository.CategoryRepositoryImpl
import com.example.taskorium.data.repository.TaskRepositoryImpl
import com.example.taskorium.domain.alarm.AlarmScheduler
import com.example.taskorium.domain.repository.AuthRepository
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule{

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        androidAlarmScheduler: AndroidAlarmScheduler
    ): AlarmScheduler
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepository: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun BindCategoryRepository(
        categoryRepository: CategoryRepositoryImpl
    ): CategoryRepository

    companion object{
        @Provides
        @Singleton
        fun provideTaskoriumDatabase(@ApplicationContext context: Context): TaskoriumDatabase{
            return Room.databaseBuilder(
                context = context,
                klass = TaskoriumDatabase::class.java,
                name = Constants.DATABASE_NAME
            ).build()
        }

        @Provides
        @Singleton
        fun provideTaskoriumDao(taskorium: TaskoriumDatabase): TaskoriumDao{
            return taskorium.taskoriumDao()
        }

        @Provides
        @Singleton
        fun provideCategoryDao(taskorium: TaskoriumDatabase): CategoryDao{
            return taskorium.categoryDao()
        }

        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences>{
            return PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile(Constants.DATASTORE_NAME) }
            )
        }
    }

}