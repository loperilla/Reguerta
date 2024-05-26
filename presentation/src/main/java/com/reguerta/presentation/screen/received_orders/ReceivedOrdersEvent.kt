package com.reguerta.presentation.screen.received_orders

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 6/2/24 at 16:38
 * All rights reserved 2024
 */

sealed class ReceivedOrdersEvent {
    data object GoOut : ReceivedOrdersEvent()
}