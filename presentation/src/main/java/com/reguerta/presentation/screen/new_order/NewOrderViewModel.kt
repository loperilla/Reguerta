package com.reguerta.presentation.screen.new_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.usecase.order.GetCurrentUserOrderUseCase
import com.reguerta.domain.usecase.orderline.AddOrderLineUseCase
import com.reguerta.domain.usecase.orderline.GetOrderLinesUseCase
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
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
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */
@HiltViewModel
class NewOrderViewModel @Inject constructor(
    getAvailableProductsUseCase: GetAvailableProductsUseCase,
    getCurrentOrderUseCase: GetCurrentUserOrderUseCase,
    getOrderLinesUseCase: GetOrderLinesUseCase,
    private val addOrderLineUseCase: AddOrderLineUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<NewOrderState> = MutableStateFlow(NewOrderState())
    val state: StateFlow<NewOrderState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            listOf(
                async {
                    getAvailableProductsUseCase().collectLatest { list ->
                        _state.update { it.copy(availableCommonProducts = list) }
                    }
                }, async {
                    getCurrentOrderUseCase().fold(
                        onSuccess = { orderId ->
                            _state.update {
                                it.copy(
                                    orderId = orderId
                                )
                            }
                            getOrderLinesUseCase(orderId).collectLatest { orderList ->
                                if (orderList.isNotEmpty()) {
                                    buildProductWithOrderList(orderList)
                                } else {
                                    _state.update {
                                        it.copy(
                                            isLoading = false
                                        )
                                    }
                                }
                            }
                        },
                        onFailure = { throwable ->
                            throwable.printStackTrace()
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            ).awaitAll()
        }
    }

    private fun buildProductWithOrderList(orderList: List<OrderLineProduct>) {
        val availableProducts = state.value.availableCommonProducts as List<CommonProduct>
        val productList: List<Product> = availableProducts.map { common ->
            val matchOrder = orderList.find { it.productId == common.id }
            if (matchOrder != null) {
                ProductWithOrderLine(
                    common,
                    matchOrder
                )
            } else {
                common
            }
        }
        _state.update {
            it.copy(
                isLoading = false,
                availableCommonProducts = productList
            )
        }
    }

    fun onEvent(newEvent: NewOrderEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (newEvent) {
                is NewOrderEvent.GoOut -> {
                    _state.update { it.copy(goOut = true) }
                }

                is NewOrderEvent.StartOrder -> {
                    addOrderLineUseCase(
                        state.value.orderId,
                        newEvent.productId
                    )
                }
            }
        }
    }
}
