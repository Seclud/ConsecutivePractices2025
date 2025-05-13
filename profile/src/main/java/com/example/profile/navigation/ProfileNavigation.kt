package com.example.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.profile.screens.ProfileEditScreen
import com.example.profile.screens.ProfileScreen

object ProfileRoutes {
    const val PROFILE_ROUTE = "profile"
    const val PROFILE_EDIT_ROUTE = "profile_edit"
}

fun NavGraphBuilder.profileGraph(navController: NavController) {
    composable(ProfileRoutes.PROFILE_ROUTE) {
        ProfileScreen(
            onEditClick = {
                navController.navigate(ProfileRoutes.PROFILE_EDIT_ROUTE)
            }
        )
    }
    composable(ProfileRoutes.PROFILE_EDIT_ROUTE) {
        ProfileEditScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onSaveComplete = {
                navController.popBackStack()
            }
        )
    }
}
