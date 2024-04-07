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
        data object RECOVERY_PASSWORD : Routes("Recovery Screen")
        data object LOGIN : Routes("Login")
        data object REGISTER : Routes("Register")
    }

    data object HOME : Routes("HomeScreen") {
        data object ROOT : Routes("Home")
        data object ORDERS : Routes("Orders")
        data object ORDER_RECEIVED : Routes("OrderReceived")

        data object SETTINGS : Routes("Settings")
    }

    data object PRODUCTS : Routes("ProductsScreen") {

        data object ROOT : Routes("Products")

        data object ADD : Routes("AddProduct")

        data object EDIT : Routes("EditProduct/{id}") {
            fun createRoute(id: String) = "EditProduct/$id"
        }
    }

    data object USERS : Routes("UserScreen") {
        data object ROOT : Routes("Users")
        data object ADD : Routes("AddUser")

        data object EDIT : Routes("EditUser/{id}") {
            fun createRoute(id: String) = "EditUser/$id"
        }
    }

    data object ORDERS : Routes("OrderScreen") {
        data object NEW : Routes("NewOrder")
    }
}
