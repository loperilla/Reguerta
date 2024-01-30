package com.reguerta.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.screen.firstscreen.firstScreen
import com.reguerta.presentation.screen.login.loginScreen
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 30/1/24 at 11:47
 * All rights reserved 2024
 */

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController(), startDestination: String) {
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
                    Text(text = "REGISTER")
                }
            }
            composable(Routes.HOME.route) {
                Text(text = "Home")
//                            HomeScreen()
            }
        }
    }
}