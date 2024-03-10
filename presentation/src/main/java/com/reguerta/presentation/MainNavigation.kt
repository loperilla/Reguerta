package com.reguerta.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.screen.auth.authGraph
import com.reguerta.presentation.screen.home.homeScreen
import com.reguerta.presentation.screen.new_order.newOrderGraph
import com.reguerta.presentation.screen.orders.ordersScreen
import com.reguerta.presentation.screen.products.productsGraph
import com.reguerta.presentation.screen.received_orders.receivedOrdersScreen
import com.reguerta.presentation.screen.settings.settingsScreen
import com.reguerta.presentation.screen.users.usersGraph
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 30/1/24 at 11:47
 * All rights reserved 2024
 */

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    Screen {
        NavHost(
            navController,
            startDestination = startDestination
        ) {
            authGraph(navController)
            navigation(startDestination = Routes.HOME.ROOT.route, route = Routes.HOME.route) {
                composable(Routes.HOME.ROOT.route) {
                    homeScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.ORDERS.route) {
                    ordersScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.ORDER_RECEIVED.route) {
                    receivedOrdersScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.SETTINGS.route) {
                    settingsScreen {
                        navController.navigate(it)
                    }
                }
            }
            newOrderGraph(navController)
            productsGraph(navController)
            usersGraph(navController)
        }
    }
}