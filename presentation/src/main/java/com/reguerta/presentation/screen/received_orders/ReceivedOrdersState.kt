package com.reguerta.presentation.screen.received_orders

import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.received.OrderLineReceived

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 6/2/24 at 16:38
 * All rights reserved 2024
 */

data class ReceivedOrdersState(
    val goOut: Boolean = false,
    val ordersByUser: Map<String, List<OrderLineReceived>> = emptyMap(),
    val ordersByProduct: Map<Product, List<OrderLineReceived>> = emptyMap(),
    val measures: List<Measure> = emptyList(),
    val containers: List<Container> = emptyList()
)
