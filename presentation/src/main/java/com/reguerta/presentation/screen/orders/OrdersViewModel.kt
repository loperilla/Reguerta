package com.reguerta.presentation.screen.orders

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
 * From: com.reguerta.presentation.screen.orders
 * Created By Manuel Lopera on 6/2/24 at 16:28
 * All rights reserved 2024
 */
@HiltViewModel
class OrdersViewModel @Inject constructor(

) : ViewModel() {
    private var _state: MutableStateFlow<OrderState> = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state

    fun onEvent(event: OrderEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is OrderEvent.GoOut -> {
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