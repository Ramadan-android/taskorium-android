package com.example.taskorium.ui.features.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskorium.route.SettingsRoute
import com.example.taskorium.ui.features.auth.toAuth

fun NavGraphBuilder.settingsRoute(navController: NavController, innerPadding: PaddingValues){
    composable<SettingsRoute>{
        SettingsScreen(
            innerPadding = innerPadding,
            navigateToAuth = { navController.toAuth() }
        )
    }
}

fun NavController.toSettings(){
    navigate(SettingsRoute)
}