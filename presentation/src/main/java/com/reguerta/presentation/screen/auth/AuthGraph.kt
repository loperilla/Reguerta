package com.reguerta.presentation.screen.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.reguerta.presentation.screen.auth.firstScreen.firstScreen
import com.reguerta.presentation.screen.auth.login.loginScreen
import com.reguerta.presentation.screen.auth.recovery.recoveryPasswordScreen
import com.reguerta.presentation.screen.auth.register.registerScreen
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.auth
 * Created By Manuel Lopera on 10/3/24 at 12:00
 * All rights reserved 2024
 */

fun NavGraphBuilder.authGraph(navController: NavHostController) {
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
        composable(Routes.AUTH.RECOVERY_PASSWORD.route) {
            recoveryPasswordScreen {
                navController.navigate(it)
            }
        }
        composable(Routes.AUTH.REGISTER.route) {
            registerScreen {
                navController.navigate(it)
            }
        }
    }
}