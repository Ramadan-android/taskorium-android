package com.example.taskorium.core.util

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.taskorium.BuildConfig

object Constants {
//    Room val
    const val CATEGORY_TABLE = "categories"
    const val TASK_TABLE = "tasks"
    const val DATABASE_NAME = "taskorium_database"


//    Api base url
    const val BASE_URL = BuildConfig.BASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

//    Api val Endpoint
    const val LOGIN_ENDPOINT = "auth/v1/token?grant_type=password"
    const val REGISTER_ENDPOINT = "auth/v1/signup"
    const val REFRESH_TOKEN_ENDPOINT = "auth/v1/token?grant_type=refresh_token"

    const val GET_TASK_ENDPOINT = "rest/v1/tasks"
    const val ADD_TASK_ENDPOINT = "rest/v1/tasks"
    const val UPDATE_TASK_ENDPOINT = "rest/v1/tasks"
    const val DELETE_TASK_ENDPOINT = "rest/v1/tasks"

    const val GET_CATEGORY_ENDPOINT = "rest/v1/categories"
    const val ADD_CATEGORY_ENDPOINT = "rest/v1/categories"
    const val UPDATE_CATEGORY_ENDPOINT = "rest/v1/categories"
    const val DELETE_CATEGORY_ENDPOINT = "rest/v1/categories"
    const val UPDATE_COMPETITION_TASK_ENDPOINT = "updateTask"



//    DataStore
    const val DATASTORE_NAME = "taskorium_prefs"
    val TOKEN_KEY = stringPreferencesKey("auth_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_auth_token")

    const val DEFAULT_CATEGORY_ID = "All"

}