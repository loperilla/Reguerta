package com.reguerta.presentation.state

import androidx.annotation.StringRes

// presentation/state/UiText.kt
sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    data class Resource(@param:StringRes @field:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
}