package com.reguerta.presentation.screen.new_order

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:08
 * All rights reserved 2024
 */

fun NavGraphBuilder.newOrderGraph(navController: NavHostController) {
    composable(Routes.ORDERS.NEW.route) {
        newOrderScreen { navController.navigate(it) }
    }
}