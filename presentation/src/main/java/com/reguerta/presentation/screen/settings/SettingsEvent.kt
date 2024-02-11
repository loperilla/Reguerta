package com.reguerta.presentation.screen.settings

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.settings
 * Created By Manuel Lopera on 6/2/24 at 16:47
 * All rights reserved 2024
 */
sealed class SettingsEvent {
    data object GoOut : SettingsEvent()
}