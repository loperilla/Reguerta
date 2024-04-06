package com.reguerta.presentation.screen.new_order

import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.received.OrderLineReceived

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */
data class NewOrderState(
    val goOut: Boolean = false,
    val isLoading: Boolean = true,
    val isExistOrder: Boolean = false,
    val hasOrderLine: Boolean = false,
    val showShoppingCart: Boolean = false,
    val availableCommonProducts: List<Product> = emptyList(),
    val productsOrderLineList: List<ProductWithOrderLine> = emptyList(),
    val ordersFromExistingOrder: Map<Product, List<OrderLineReceived>> = emptyMap()
)
