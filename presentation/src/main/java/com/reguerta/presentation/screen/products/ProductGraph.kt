package com.reguerta.presentation.screen.products

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.reguerta.presentation.screen.products.add.addProductScreen
import com.reguerta.presentation.screen.products.edit.editProductScreen
import com.reguerta.presentation.screen.products.root.productScreen
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.products
 * Created By Manuel Lopera on 10/3/24 at 11:56
 * All rights reserved 2024
 */

fun NavGraphBuilder.productsGraph(navController: NavHostController) {
    navigation(startDestination = Routes.PRODUCTS.ROOT.route, route = Routes.PRODUCTS.route) {
        composable(Routes.PRODUCTS.ROOT.route) {
            productScreen {
                navController.navigate(it)
            }
        }
        composable(Routes.PRODUCTS.ADD.route) {
            addProductScreen {
                navController.popBackStack(
                    Routes.PRODUCTS.ROOT.route,
                    inclusive = false
                )
            }
        }

        composable(
            route = Routes.PRODUCTS.EDIT.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) {
            val id = it.arguments?.getString("id").orEmpty()
            editProductScreen(id) {
                navController.popBackStack(
                    Routes.PRODUCTS.ROOT.route,
                    inclusive = false
                )
            }
        }
    }
}