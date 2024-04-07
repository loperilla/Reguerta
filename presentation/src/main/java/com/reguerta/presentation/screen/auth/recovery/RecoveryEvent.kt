package com.reguerta.presentation.screen.auth.recovery

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.auth.recovery
 * Created By Manuel Lopera on 7/4/24 at 15:53
 * All rights reserved 2024
 */
sealed class RecoveryEvent {
    data class EmailChanged(val email: String) : RecoveryEvent()
    data object SendEmail : RecoveryEvent()
    data object GoBack : RecoveryEvent()
}