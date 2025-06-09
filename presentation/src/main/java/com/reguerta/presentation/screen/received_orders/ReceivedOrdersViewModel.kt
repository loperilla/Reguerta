package com.reguerta.presentation.screen.received_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.fullOrderName
import com.reguerta.domain.usecase.containers.GetAllContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.OrderReceivedModel
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
import timber.log.Timber
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
    private val getAllContainersUseCase: GetAllContainersUseCase,
    private val orderReceivedModel: OrderReceivedModel
) : ViewModel() {
    private var _state: MutableStateFlow<ReceivedOrdersState> =
        MutableStateFlow(ReceivedOrdersState())
    val state: StateFlow<ReceivedOrdersState> = _state.asStateFlow()

    init {
        // 1. Pone loader
        _state.update { it.copy(isLoading = true) }
        // 2. Carga medidas y contenedores en paralelo
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                val measureList = getAllMeasuresUseCase()
                _state.update { it.copy(measures = measureList) }
            }
            launch {
                val containerList = getAllContainersUseCase()
                _state.update { it.copy(containers = containerList) }
            }
        }
        // 3. Observa el Flow de pedidos recibidos (¡reactivo!)
        viewModelScope.launch(Dispatchers.IO) {
            orderReceivedModel.invoke().collectLatest { orders ->
                _state.update {
                    it.copy(
                        isLoading = false, // loader solo se quita aquí
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
    fun forceReload() {
        Timber.i("SYNC: forceReload lanzada en ${this::class.simpleName} a las ${System.currentTimeMillis()}")
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            listOf(
                async {
                    val measureList = getAllMeasuresUseCase()
                    Timber.i("SYNC: collect de MEASURES en ${this@ReceivedOrdersViewModel::class.simpleName} a las ${System.currentTimeMillis()} - tamaño: ${measureList.size}")
                    _state.update {
                        it.copy(measures = measureList)
                    }
                },
                async {
                    val containerList = getAllContainersUseCase()
                    Timber.i("SYNC: collect de CONTAINERS en ${this@ReceivedOrdersViewModel::class.simpleName} a las ${System.currentTimeMillis()} - tamaño: ${containerList.size}")
                    _state.update {
                        it.copy(containers = containerList)
                    }
                },
                async {
                    orderReceivedModel.invoke().collectLatest { orders ->
                        Timber.i("SYNC: collect de ORDERS en ${this@ReceivedOrdersViewModel::class.simpleName} a las ${System.currentTimeMillis()} - tamaño: ${orders.size}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                ordersByUser = orders.groupBy { it.fullOrderName() },
                                ordersByProduct = orders.groupBy { it.product }
                            )
                        }
                    }
                }
            ).awaitAll()
            _state.update { it.copy(isLoading = false) }
        }
    }
}
