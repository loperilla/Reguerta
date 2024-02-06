package com.reguerta.presentation.screen.home

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:49
 * All rights reserved 2024
 */
sealed class HomeEvent {
    data object GoOut : HomeEvent()
    data object GoHome : HomeEvent()
    data object GoOrders : HomeEvent()
    data object GoOrderReceived : HomeEvent()
    data object GoProducts : HomeEvent()
    data object GoUsers : HomeEvent()
    data object GoSettings : HomeEvent()
    data object ShowDialog : HomeEvent()
    data object HideDialog : HomeEvent()
}
