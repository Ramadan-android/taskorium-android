package com.example.taskorium.ui.features.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskorium.route.AuthRoute
import com.example.taskorium.ui.features.home.toHome

fun NavGraphBuilder.authRoute(navController: NavController,innerPadding: PaddingValues){
    composable<AuthRoute>{ AuthScreen(
        navigateToHome = {navController.toHome()},
        innerPadding = innerPadding
    ) }
}

fun NavController.toAuth(){
    navigate(AuthRoute)
}