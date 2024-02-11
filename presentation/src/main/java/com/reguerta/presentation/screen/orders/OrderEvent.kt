package com.reguerta.presentation.screen.orders

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.orders
 * Created By Manuel Lopera on 6/2/24 at 16:29
 * All rights reserved 2024
 */
sealed class OrderEvent {
    data object GoOut : OrderEvent()
}