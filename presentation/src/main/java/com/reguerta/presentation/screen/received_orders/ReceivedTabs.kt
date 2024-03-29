package com.reguerta.presentation.screen.received_orders

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 25/3/24 at 20:39
 * All rights reserved 2024
 */
data class ReceivedTab(
    val title: String
)

val tabList = listOf(
    ReceivedTab("Por producto"),
    ReceivedTab("Por regüertense")
)