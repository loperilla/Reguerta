package com.reguerta.presentation.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.settings
 * Created By Manuel Lopera on 6/2/24 at 16:46
 * All rights reserved 2024
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private var _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state
    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.GoOut -> {
                _state.value = SettingsState(goOut = true)
            }
        }
    }
}