package com.reguerta.presentation.screen.new_order

import com.reguerta.domain.model.interfaces.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */
data class NewOrderState(
    val goOut: Boolean = false,
    val isLoading: Boolean = true,
    val orderId: String = "",
    val availableCommonProducts: List<Product> = emptyList()
)
