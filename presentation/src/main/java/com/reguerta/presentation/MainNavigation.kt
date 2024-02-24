package com.reguerta.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.screen.add_user.addUserScreen
import com.reguerta.presentation.screen.edit_user.editUserScreen
import com.reguerta.presentation.screen.firstscreen.firstScreen
import com.reguerta.presentation.screen.home.homeScreen
import com.reguerta.presentation.screen.login.loginScreen
import com.reguerta.presentation.screen.orders.ordersScreen
import com.reguerta.presentation.screen.received_orders.receivedOrdersScreen
import com.reguerta.presentation.screen.register.registerScreen
import com.reguerta.presentation.screen.settings.settingsScreen
import com.reguerta.presentation.screen.users.usersScreen
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
            navigation(startDestination = Routes.AUTH.FIRST_SCREEN.route, route = Routes.AUTH.route) {
                composable(Routes.AUTH.FIRST_SCREEN.route) {
                    firstScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.AUTH.LOGIN.route) {
                    loginScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.AUTH.REGISTER.route) {
                    registerScreen {
                        navController.navigate(it)
                    }
                }
            }
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
                composable(Routes.HOME.PRODUCTS.route) {
                    Text("Products")
                }
            }

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
    }
}