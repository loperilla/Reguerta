package com.reguerta.presentation.screen.received_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.received.OrderLineReceived
import com.reguerta.domain.model.received.fullOrderName
import com.reguerta.domain.usecase.container.GetAllContainerUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderline.OrderReceivedModel
import com.reguerta.presentation.getContainerByNameOrPlural
import com.reguerta.presentation.getMeasureByNameOrPlural
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
/*
@HiltViewModel
class ReceivedOrdersViewModel @Inject constructor(
    getAllMeasuresUseCase: GetAllMeasuresUseCase,
    getAllContainerUseCase: GetAllContainerUseCase,
    private val orderReceivedModel: OrderReceivedModel
) : ViewModel() {
    private var _state: MutableStateFlow<ReceivedOrdersState> = MutableStateFlow(ReceivedOrdersState())
    val state: StateFlow<ReceivedOrdersState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            orderReceivedModel.invoke().collectLatest { orders ->
                _state.update { state ->
                    state.copy(
                        ordersByUser = orders.groupBy { it.fullOrderName() },
                        ordersByProduct = orders.groupBy { it.product }
                    )
                }
            }
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


 */
@HiltViewModel
class ReceivedOrdersViewModel @Inject constructor(
    private val getAllMeasuresUseCase: GetAllMeasuresUseCase,
    private val getAllContainerUseCase: GetAllContainerUseCase,
    private val orderReceivedModel: OrderReceivedModel
) : ViewModel() {
    private var _state: MutableStateFlow<ReceivedOrdersState> = MutableStateFlow(ReceivedOrdersState())
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

    fun getQuantitySum(line: OrderLineReceived): String {
        val container = getContainerByNameOrPlural(line.product.container, state.value.containers)
        val measure = getMeasureByNameOrPlural(line.product.unity, state.value.measures)

        return if (container != null && measure != null) {
            if (container.name == "A granel") {
                val sum = line.quantity * line.product.quantityWeight
                "$sum ${measure.abbreviation}"
            } else {
                if (line.quantity > 1) container.plural else container.name
            }
        } else {
            ""
        }
    }
}

