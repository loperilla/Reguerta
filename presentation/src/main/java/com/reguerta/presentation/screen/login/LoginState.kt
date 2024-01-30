package com.reguerta.presentation.screen.login

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:45
 * All rights reserved 2024
 */
data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val emailInput: String = "",
    val passwordInput: String = ""
)
