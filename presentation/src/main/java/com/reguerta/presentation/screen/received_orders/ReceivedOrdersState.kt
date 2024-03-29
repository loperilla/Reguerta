package com.reguerta.presentation.screen.received_orders

import com.reguerta.domain.model.OrderLineReceived

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 6/2/24 at 16:38
 * All rights reserved 2024
 */
data class ReceivedOrdersState(
    val goOut: Boolean = false,
    val orders: List<OrderLineReceived> = emptyList()
)
