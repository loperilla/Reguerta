package com.reguerta.presentation.screen.received_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 6/2/24 at 16:37
 * All rights reserved 2024
 */
@HiltViewModel
class ReceivedOrdersViewModel @Inject constructor() : ViewModel() {
    private var _state: MutableStateFlow<ReceivedOrdersState> = MutableStateFlow(ReceivedOrdersState())
    val state: StateFlow<ReceivedOrdersState> = _state

    fun onEvent(event: ReceivedOrdersEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is ReceivedOrdersEvent.GoOut -> {
                    _state.update {
                        it.copy(
                            goOut = true
                        )
                    }
                }
            }
        }
    }
}
