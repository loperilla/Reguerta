package com.reguerta.presentation.screen.home

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:49
 * All rights reserved 2024
 */
sealed class HomeEvent {
    data object GoOut : HomeEvent()
}
