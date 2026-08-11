package com.example.taskorium.route

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.taskorium.ui.features.addTask.addTaskRoute
import com.example.taskorium.ui.features.auth.authRoute
import com.example.taskorium.ui.features.editDeleteCat.editDeleteCatRoute
import com.example.taskorium.ui.features.home.homeRoute
import com.example.taskorium.ui.features.settings.settingsRoute
import com.example.taskorium.ui.features.splashView.StartDestination

@Composable
fun TaskoriumNavGraph(
    navController: NavHostController,
    startDestination: StartDestination,
    innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = when(startDestination){
            StartDestination.Auth -> AuthRoute
            StartDestination.Home -> HomeRoute
        }
    ) {
        authRoute(navController,innerPadding)
        homeRoute(navController)
        addTaskRoute(navController, innerPadding)
        editDeleteCatRoute(navController, innerPadding)
        settingsRoute(navController, innerPadding)
    }
}