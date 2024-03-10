package com.reguerta.presentation.screen.auth.login

import com.reguerta.presentation.type.Email
import com.reguerta.presentation.type.Password

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:45
 * All rights reserved 2024
 */
data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val emailInput: Email = "",
    val passwordInput: Password = "",
    val enabledButton: Boolean = false,
    val goOut: Boolean = false
)
