package com.reguerta.presentation.screen.users

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.reguerta.presentation.screen.users.add.addUserScreen
import com.reguerta.presentation.screen.users.edit.editUserScreen
import com.reguerta.presentation.screen.users.root.usersScreen
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 10/3/24 at 11:58
 * All rights reserved 2024
 */

fun NavGraphBuilder.usersGraph(navController: NavHostController) {
    navigation(startDestination = Routes.USERS.ROOT.route, route = Routes.USERS.route) {
        composable(Routes.USERS.ROOT.route) {
            usersScreen {
                navController.navigate(it)
            }
        }

        composable(Routes.USERS.ADD.route) {
            addUserScreen {
                navController.popBackStack(
                    Routes.USERS.ROOT.route,
                    inclusive = false
                )
            }
        }

        composable(
            route = Routes.USERS.EDIT.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            val id = it.arguments?.getString("id").orEmpty()
            editUserScreen(id) {
                navController.popBackStack(
                    Routes.USERS.ROOT.route,
                    inclusive = false
                )
            }
        }
    }
}