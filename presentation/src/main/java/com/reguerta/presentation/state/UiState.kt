package com.reguerta.presentation.state

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 23/1/24 at 20:37
 * All rights reserved 2024
 */
sealed class UiState {
    data object Loading : UiState()
    data object Success : UiState()
    data object Error : UiState()
}