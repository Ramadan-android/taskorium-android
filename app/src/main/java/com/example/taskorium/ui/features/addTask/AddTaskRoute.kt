package com.example.taskorium.ui.features.addTask

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskorium.route.AddEditTaskRoute


fun NavGraphBuilder.addTaskRoute(navController: NavController, innerPadding: PaddingValues){

    composable<AddEditTaskRoute> {backStackEntry ->
        AddTaskScreen(
            navigateToHomeScreen = {
                navController.popBackStack()
            },
            innerPadding = innerPadding
        )
    }
}



fun NavController.toAddTask(){
    navigate(AddEditTaskRoute())
}

fun NavController.toEditTask(taskId: String){
    navigate(AddEditTaskRoute(taskId = taskId))
}