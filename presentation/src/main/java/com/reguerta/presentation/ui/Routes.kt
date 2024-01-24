package com.reguerta.presentation.ui

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.ui
 * Created By Manuel Lopera on 23/1/24 at 20:46
 * All rights reserved 2024
 */
sealed class Routes(val route: String) {
    data object AUTH : Routes("AuthScreen") {
        data object FIRST_SCREEN : Routes("First Screen")
        data object LOGIN : Routes("Login")
        data object REGISTER : Routes("Register")
    }

    data object HOME : Routes("HomeScreen")
}
