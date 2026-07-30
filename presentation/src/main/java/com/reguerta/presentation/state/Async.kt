package com.reguerta.presentation.state

// presentation/state/Async.kt
sealed interface Async<out T> {
    data object Uninitialized : Async<Nothing>
    data object Loading : Async<Nothing>
    data class Success<T>(val data: T) : Async<T>
    data class Error(val message: UiText) : Async<Nothing>
}