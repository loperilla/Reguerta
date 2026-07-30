package com.reguerta.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class FieldError(val message: UiText? = null) {
    val hasError get() = message != null
}