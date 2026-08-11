package com.example.taskorium.route

import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute

@Serializable
data object HomeRoute
@Serializable
data object SettingsRoute
@Serializable

data class AddEditTaskRoute(val taskId: String? = null)
@Serializable

data class EditDeleteCategoryRoute(val catId: String? = null)


