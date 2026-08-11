package com.example.taskorium.ui.features.editDeleteCat

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskorium.route.EditDeleteCategoryRoute

fun NavGraphBuilder.editDeleteCatRoute(navController: NavController, innerPadding: PaddingValues){

    composable<EditDeleteCategoryRoute> {
        EditDeleteCatHome(

            navigateToHomeScreen = {
                navController.popBackStack()
            },
            innerPadding = innerPadding
        )
    }
}

fun NavController.toEditDeleteCat(catId: String){
    navigate(EditDeleteCategoryRoute(catId))
}