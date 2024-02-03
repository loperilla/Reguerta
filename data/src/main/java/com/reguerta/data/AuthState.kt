package com.reguerta.data

/*****
 * Project: Reguerta
 * From: com.reguerta.data
 * Created By Manuel Lopera on 24/1/24 at 11:54
 * All rights reserved 2024
 */
sealed class AuthState {
    data object LoggedIn : AuthState()
    data class Error(val message: String) : AuthState()
}
