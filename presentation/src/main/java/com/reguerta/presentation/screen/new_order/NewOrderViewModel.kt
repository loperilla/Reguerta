package com.reguerta.presentation.screen.new_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.new_order.NewOrderModel
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.container.GetAllContainerUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
import com.reguerta.domain.usecase.products.UpdateProductStockUseCase
import com.reguerta.domain.usecase.week.GetCurrentWeekDayUseCase
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
import java.time.DayOfWeek
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val getAvailableProductsUseCase: GetAvailableProductsUseCase,
    private val getCurrentWeek: GetCurrentWeekDayUseCase,
    private val getAllMeasuresUseCase: GetAllMeasuresUseCase,
    private val getAllContainerUseCase: GetAllContainerUseCase,
    private val orderModel: NewOrderModel,
    private val updateProductStockUseCase: UpdateProductStockUseCase,
    private val checkCurrentUserLoggedUseCase: CheckCurrentUserLoggedUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<NewOrderState> = MutableStateFlow(NewOrderState())
    val state: StateFlow<NewOrderState> = _state.asStateFlow()

    private lateinit var initialCommonProducts: List<CommonProduct>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserResult = checkCurrentUserLoggedUseCase()
            val currentUser = currentUserResult.getOrNull()

            if (currentUser == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            _state.update {
                it.copy(
                    currentDay = DayOfWeek.of(getCurrentWeek())
                )
            }

            listOf(
                async {
                    loadAvailableProducts(getAvailableProductsUseCase)
                },
                async {
                    getAllMeasuresUseCase().collect { measureList ->
                        _state.update {
                            it.copy(measures = measureList)
                        }
                    }
                },
                async  {
                    getAllContainerUseCase().collect { containerList ->
                        _state.update {
                            it.copy(containers = containerList)
                        }
                    }
                },
                async {
                    val currentDay = DayOfWeek.of(getCurrentWeek())
                    if (currentDay in DayOfWeek.MONDAY..DayOfWeek.WEDNESDAY) {
                        handleLastWeekOrders()
                    }
                    handleCurrentWeekOrders()
                }
            ).awaitAll()
        }
    }

    private suspend fun loadAvailableProducts(getAvailableProductsUseCase: GetAvailableProductsUseCase) {
        getAvailableProductsUseCase().collectLatest { list ->
            initialCommonProducts = list
            val groupedByCompany = list.groupBy { it.companyName }.toSortedMap()
            _state.update {
                it.copy(
                    isLoading = false,
                    productsGroupedByCompany = groupedByCompany
                )
            }
        }
    }

    private suspend fun handleLastWeekOrders() {
        orderModel.checkIfExistLastWeekOrderInFirebase().fold(
            onSuccess = { existOrder ->
                _state.update { it.copy(isExistOrder = existOrder) }
                if (existOrder) {
                    loadOrderLinesFromCurrentWeek()
                }
            },
            onFailure = { handleError(it) }
        )
    }

    private suspend fun handleCurrentWeekOrders() {
        orderModel.checkIfExistOrderInFirebase().fold(
            onSuccess = { existOrder ->
                _state.update { it.copy(isExistOrder = existOrder) }
                if (existOrder) {
                    loadOrderLinesFromCurrentWeek()
                }
                else {
                    loadNewOrderLines()
                }
            },
            onFailure = { handleError(it) }
        )
    }

    private suspend fun loadOrderLinesFromCurrentWeek() {
        orderModel.getOrderLinesFromCurrentWeek().collectLatest { ordersReceived ->
            val groupedByCompany = ordersReceived.groupBy { it.companyName }.toSortedMap()
            _state.update {
                it.copy(
                    isLoading = false,
                    hasOrderLine = true,
                    orderLinesByCompanyName = groupedByCompany,
                    ordersFromExistingOrder = ordersReceived.groupBy { orderLine -> orderLine.product }
                )
            }
        }
    }

    private suspend fun loadNewOrderLines() {
        orderModel.getOrderLines().collectLatest { orderList ->
            if (orderList.isNotEmpty()) {
                buildProductWithOrderList(orderList)
            } else {
                _state.update { it ->
                    it.copy(
                        isLoading = false,
                        productsGroupedByCompany = initialCommonProducts.groupBy { it.companyName }.toSortedMap(),
                        hasOrderLine = false
                    )
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
        _state.update { it.copy(isLoading = false) }
    }

    private fun buildProductWithOrderList(orderList: List<OrderLineProduct>) {
        val productList = initialCommonProducts.map { common ->
            orderList.find { it.productId == common.id }?.let { order ->
                ProductWithOrderLine(common, order)
            } ?: common
        }
        val productsWithOrderLine = productList.filterIsInstance<ProductWithOrderLine>()
        val groupedByCompany = productList.groupBy { it.companyName }.toSortedMap()

        _state.update {
            it.copy(
                isLoading = false,
                hasOrderLine = productsWithOrderLine.isNotEmpty(),
                productsGroupedByCompany = groupedByCompany,
                productsOrderLineList = productsWithOrderLine
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
                    val selectedProduct = state.value.productsGroupedByCompany.values.flatten().find { it.id == newEvent.productId }
                    selectedProduct?.let {
                        orderModel.addLocalOrderLine(
                            newEvent.productId,
                            it.companyName
                        )
                    }
                }
                is NewOrderEvent.PlusQuantityProduct -> {
                    val productUpdated = state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.plus(1)
                        orderModel.updateProductStock(newEvent.productId, newQuantity)
                        _state.update { currentState ->
                            val newList = currentState.productsOrderLineList.map { if (it.id == newEvent.productId) it.copy(orderLine = it.orderLine.copy(quantity = newQuantity)) else it }
                            currentState.copy(productsOrderLineList = newList)
                        }
                    }
                }
                is NewOrderEvent.MinusQuantityProduct -> {
                    val productUpdated = state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.minus(1)
                        if (newQuantity == 0) {
                            orderModel.deleteOrderLineLocal(newEvent.productId)
                            _state.update { currentState ->
                                currentState.copy(productsOrderLineList = currentState.productsOrderLineList.filter { it.id != newEvent.productId })
                            }
                        } else {
                            orderModel.updateProductStock(newEvent.productId, newQuantity)
                            _state.update { currentState ->
                                val newList = currentState.productsOrderLineList.map { if (it.id == newEvent.productId) it.copy(orderLine = it.orderLine.copy(quantity = newQuantity)) else it }
                                currentState.copy(productsOrderLineList = newList)
                            }
                        }
                    }
                }

                NewOrderEvent.HideShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = false) }
                }

                NewOrderEvent.ShowShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = true) }
                }

                NewOrderEvent.PushOrder -> {
                    val productsToPush = state.value.productsGroupedByCompany.values.flatten()
                        .filterIsInstance<ProductWithOrderLine>()

                    orderModel.pushOrderLinesToFirebase(productsToPush).fold(
                        onSuccess = {
                            _state.update { it.copy(showPopup = PopupType.ORDER_ADDED) }
                        },
                        onFailure = { throwable ->
                            throwable.printStackTrace()
                        }
                    )
                }

                NewOrderEvent.DeleteOrder -> {
                    for (orderLines in state.value.ordersFromExistingOrder.values.flatten()) {
                        val product = initialCommonProducts.find { it.id == orderLines.product.id }
                        product?.let {
                            updateProductStockUseCase(
                                it.id,
                                it.stock.plus(orderLines.quantity)
                            )
                        }
                    }
                    orderModel.deleteOrder()
                    _state.update { it.copy(goOut = true) }
                }

                NewOrderEvent.HideDialog -> _state.update { it.copy(showPopup = PopupType.NONE) }
                NewOrderEvent.ShowAreYouSureDeleteOrder -> _state.update { it.copy(showPopup = PopupType.ARE_YOU_SURE_DELETE) }
            }
        }
    }

}
