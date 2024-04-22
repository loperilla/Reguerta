package com.reguerta.presentation.screen.auth.recovery

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.auth.recovery
 * Created By Manuel Lopera on 7/4/24 at 15:53
 * All rights reserved 2024
 */
data class RecoveryPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showFailureDialog: Boolean = false,
    val goOut: Boolean = false
)