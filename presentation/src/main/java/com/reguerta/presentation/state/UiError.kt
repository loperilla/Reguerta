package com.reguerta.presentation.state

import androidx.compose.runtime.Immutable

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.input
 * Created By Manuel Lopera on 2/2/24 at 20:58
 * All rights reserved 2024
 */


/**
 * Estado de error de campo en UI.
 *
 * NOTA: históricamente `isVisible` tenía semántica invertida:
 * hay error cuando `isVisible == false`.
 * Para evitar confusiones durante la migración a FieldError/UiText, usa `hasError`.
 */
@Immutable
data class UiError(
    val message: String = "",
    val isVisible: Boolean = true
)

/** Verdadero cuando hay error (equivale a `!isVisible`). */
val UiError.hasError: Boolean
    get() = !isVisible

/** Devuelve el mensaje como UiText dinámico cuando hay error; si no, null. */
fun UiError.asUiTextOrNull(): UiText? =
    if (hasError && message.isNotBlank()) UiText.Dynamic(message) else null