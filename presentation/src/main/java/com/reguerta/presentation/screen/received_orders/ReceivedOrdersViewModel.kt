package com.reguerta.presentation.screen.received_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.fullOrderName
import com.reguerta.domain.usecase.containers.GetAllContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.OrderReceivedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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

    private val _state: MutableStateFlow<ReceivedOrdersState> =
        MutableStateFlow(ReceivedOrdersState())
    val state: StateFlow<ReceivedOrdersState> = _state.asStateFlow()

    private var ordersJob: Job? = null
    private var hasForcedReload = false

    init {
        // Carga datos auxiliares (medidas/containers) y arranca el colector de pedidos.
        loadMeasuresAndContainers()
        startOrdersCollector()
    }

    private fun loadMeasuresAndContainers() {
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                val measuresDeferred = async {
                    runCatching { getAllMeasuresUseCase() }
                        .getOrElse {
                            Timber.e(it, "Error loading measures")
                            emptyList()
                        }
                }
                val containersDeferred = async {
                    runCatching { getAllContainersUseCase() }
                        .getOrElse {
                            Timber.e(it, "Error loading containers")
                            emptyList()
                        }
                }

                val measures = measuresDeferred.await()
                val containers = containersDeferred.await()

                _state.update { it.copy(measures = measures, containers = containers) }
            }
        }
    }

    private fun startOrdersCollector() {
        ordersJob?.cancel()
        ordersJob = viewModelScope.launch {
            orderReceivedModel.invoke()
                .flowOn(Dispatchers.IO)
                .onStart {
                    _state.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    Timber.e(e, "Error collecting received orders")
                    _state.update { it.copy(isLoading = false) }
                }
                .collectLatest { orders ->
                    _state.update {
                        it.copy(
                            isLoading = false,
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
                    _state.update { it.copy(goOut = true) }
                }
            }
        }
    }

    /**
     * Útil cuando esta pantalla se reanuda sin pasar por Home (restore del backstack).
     * No crea colectores infinitos adicionales: reinicia el único colector y recarga auxiliares.
     */
    fun forceReload() {
        Timber.i(
            "SYNC: forceReload lanzada en %s a las %s",
            this::class.simpleName,
            System.currentTimeMillis()
        )
        _state.update { it.copy(isLoading = true) }
        loadMeasuresAndContainers()
        startOrdersCollector()
    }

    fun forceReloadOnce() {
        if (hasForcedReload) {
            Timber.i("SYNC: forceReloadOnce ya ejecutada, se omite")
            return
        }
        hasForcedReload = true
        forceReload()
    }
}
