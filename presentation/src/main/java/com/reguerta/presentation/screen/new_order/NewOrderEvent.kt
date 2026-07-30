package com.reguerta.presentation.screen.new_order

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */

sealed class NewOrderEvent {
    data class StartOrder(val productId: String) : NewOrderEvent()
    data class PlusQuantityProduct(val productId: String) : NewOrderEvent()
    data class MinusQuantityProduct(val productId: String) : NewOrderEvent()
    data object GoOut : NewOrderEvent()
    data object ShowShoppingCart : NewOrderEvent()
    data object HideShoppingCart : NewOrderEvent()
    data object PushOrder : NewOrderEvent()
    data object ShowAreYouSureDeleteOrder : NewOrderEvent()
    data object HideDialog : NewOrderEvent()
    data object DeleteOrder : NewOrderEvent()
    data object ShowSearch : NewOrderEvent()
    data object HideSearch : NewOrderEvent()
    data class UpdateSearchQuery(val query: String) : NewOrderEvent()
}