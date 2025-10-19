package com.reguerta.presentation.state

// presentation/state/UiText.kt
sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    data class Resource(@androidx.annotation.StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
}