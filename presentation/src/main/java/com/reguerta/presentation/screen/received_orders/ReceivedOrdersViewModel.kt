package com.reguerta.presentation.screen.received_orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.received.fullOrderName
import com.reguerta.domain.usecase.container.GetAllContainerUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderline.OrderReceivedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
class ReceivedOrdersViewModel @Inject constructor(
    private val getAllMeasuresUseCase: GetAllMeasuresUseCase,
    private val getAllContainerUseCase: GetAllContainerUseCase,
    private val orderReceivedModel: OrderReceivedModel
) : ViewModel() {
    private var _state: MutableStateFlow<ReceivedOrdersState> =
        MutableStateFlow(ReceivedOrdersState())
    val state: StateFlow<ReceivedOrdersState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listOf(
                async {
                    getAllMeasuresUseCase().collect { measureList ->
                        _state.update {
                            it.copy(measures = measureList)
                        }
                    }
                },
                async {
                    getAllContainerUseCase().collect { containerList ->
                        _state.update {
                            it.copy(containers = containerList)
                        }
                    }
                },
                async {
                    orderReceivedModel.invoke().collectLatest { orders ->
                        Log.d("OrderLines", "${orders.size} Pedidos recibidos")
                        if (orders.isEmpty()) {
                            Log.e("OrderLines", "La lista de pedidos está vacía, no se encontraron órdenes.")
                        } else {
                            Log.d("OrderLines", "Pedidos: ${orders.map { it.product.name }}")
                        }
                        _state.update { state ->
                            state.copy(
                                ordersByUser = orders.groupBy { it.fullOrderName() },
                                ordersByProduct = orders.groupBy { it.product }
                            )
                        }
                    }
                }
            ).awaitAll()
        }
    }

    fun onEvent(event: ReceivedOrdersEvent) {
        viewModelScope.launch {
            when (event) {
                ReceivedOrdersEvent.GoOut -> {
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
