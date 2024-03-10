package com.reguerta.presentation.screen.auth.register

import com.reguerta.presentation.type.Email
import com.reguerta.presentation.type.Password

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.register
 * Created By Manuel Lopera on 30/1/24 at 20:11
 * All rights reserved 2024
 */
data class RegisterState(
    val errorMessage: String = "",
    val emailInput: Email = "",
    val passwordInput: Password = "",
    val repeatPasswordInput: Password = "",
    val enabledButton: Boolean = false,
    val goOut: Boolean = false
)
