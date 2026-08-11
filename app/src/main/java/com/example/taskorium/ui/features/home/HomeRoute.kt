package com.example.taskorium.ui.features.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskorium.route.AddEditTaskRoute
import com.example.taskorium.route.HomeRoute
import com.example.taskorium.ui.features.addTask.toAddTask
import com.example.taskorium.ui.features.addTask.toEditTask
import com.example.taskorium.ui.features.editDeleteCat.toEditDeleteCat
import com.example.taskorium.ui.features.settings.toSettings

fun NavGraphBuilder.homeRoute(navController: NavController){
    composable<HomeRoute>{HomeScreen(
        navigateToAddScreen = {
            if (it == AddEditTaskRoute.toString()){
                navController.toAddTask()

            }else{
                navController.toEditTask(it)
            }
        },
        navigateToEditDeleteCatScreen = {
            navController.toEditDeleteCat(it)
        },
        navigateToSettingsScreen = {
            navController.toSettings()
        },
    )}
}

fun NavController.toHome(){
    navigate(HomeRoute)
}