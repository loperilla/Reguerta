package com.reguerta.presentation.screen.login

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:49
 * All rights reserved 2024
 */
sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
    data object SnackbarHide : LoginEvent()
}
