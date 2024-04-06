package com.reguerta.presentation.screen.new_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.new_order.NewOrderModel
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
import com.reguerta.domain.usecase.products.UpdateProductStockUseCase
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
    private val orderModel: NewOrderModel,
    private val updateProductStockUseCase: UpdateProductStockUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<NewOrderState> = MutableStateFlow(NewOrderState())
    val state: StateFlow<NewOrderState> = _state.asStateFlow()

    private lateinit var initialCommonProducts: List<CommonProduct>

    init {
        viewModelScope.launch {
            listOf(
                async {
                    getAvailableProductsUseCase().collectLatest { list ->
                        initialCommonProducts = list
                    }
                }, async(Dispatchers.IO) {
                    orderModel.checkIfExistOrderInFirebase().fold(
                        onSuccess = { existOrder ->
                            _state.update {
                                it.copy(
                                    isExistOrder = existOrder
                                )
                            }
                            if (existOrder) {
                                orderModel.getOrderLinesFromCurrentWeek().collectLatest { ordersReceived ->
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            hasOrderLine = true,
                                            ordersFromExistingOrder = ordersReceived.groupBy { orderLine ->
                                                orderLine.product
                                            }
                                        )
                                    }
                                }
                            } else {
                                orderModel.getOrderLines().collectLatest { orderList ->
                                    if (orderList.isNotEmpty()) {
                                        buildProductWithOrderList(orderList)
                                    } else {
                                        _state.update {
                                            it.copy(
                                                isLoading = false,
                                                availableCommonProducts = initialCommonProducts,
                                                hasOrderLine = false
                                            )
                                        }
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
        val productList: List<Product> = initialCommonProducts.map { common ->
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
        val listWithOrder = productList.filterIsInstance(ProductWithOrderLine::class.java)
        _state.update {
            it.copy(
                isLoading = false,
                hasOrderLine = true,
                availableCommonProducts = productList,
                productsOrderLineList = listWithOrder.ifEmpty {
                    emptyList()
                }
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
                    val companyOfSelectedProduct = state.value.availableCommonProducts.single {
                        it.id == newEvent.productId
                    }.companyName
                    orderModel.addLocalOrderLine(
                        newEvent.productId,
                        companyOfSelectedProduct
                    )
                }

                is NewOrderEvent.MinusQuantityProduct -> {
                    val productUpdated = state.value.productsOrderLineList.singleOrNull {
                        it.id == newEvent.productId
                    } ?: return@launch

                    val newQuantity = productUpdated.quantity.minus(1)

                    if (newQuantity == 0) {
                        orderModel.deleteOrderLineLocal(newEvent.productId)
                        _state.update {
                            it.copy(
                                productsOrderLineList = it.productsOrderLineList.filter { orderLine ->
                                    orderLine.id != newEvent.productId
                                }
                            )
                        }
                    } else {
                        orderModel.updateProductStock(
                            newEvent.productId,
                            newQuantity
                        )
                    }
                }

                is NewOrderEvent.PlusQuantityProduct -> {
                    val productUpdated = state.value.availableCommonProducts.singleOrNull {
                        it.id == newEvent.productId
                    } as ProductWithOrderLine? ?: return@launch

                    orderModel.updateProductStock(
                        newEvent.productId,
                        productUpdated.quantity.plus(1)
                    )
                }

                NewOrderEvent.HideShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = false) }
                }

                NewOrderEvent.ShowShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = true) }
                }

                NewOrderEvent.PushOrder -> {
                    orderModel.pushOrderLinesToFirebase(
                        state.value.productsOrderLineList
                    ).fold(
                        onSuccess = {
                            state.value.productsOrderLineList.forEach { orderLine ->
                                val productModified = initialCommonProducts.single { commonProduct ->
                                    commonProduct.id == orderLine.id
                                }
                                updateProductStockUseCase(
                                    orderLine.id,
                                    productModified.stock.minus(orderLine.quantity)
                                )
                            }
                            _state.update { it.copy(goOut = true) }
                        },
                        onFailure = { throwable ->
                            throwable.printStackTrace()
                        }
                    )
                }
            }
        }
    }
}
