package com.reguerta.presentation.screen.auth.register

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.register
 * Created By Manuel Lopera on 30/1/24 at 20:43
 * All rights reserved 2024
 */
sealed class RegisterEvent {
    data class OnEmailChanged(val email: String) : RegisterEvent()
    data class OnPasswordChanged(val password: String) : RegisterEvent()
    data class OnRepeatPasswordChanged(val repeatPassword: String) : RegisterEvent()
    data object OnRegisterClick : RegisterEvent()
    data object SnackbarHide : RegisterEvent()
}