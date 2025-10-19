package com.reguerta.presentation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.input
 * Created By Manuel Lopera on 1/2/24 at 20:28
 * All rights reserved 2024
 */
/**
 * DEPRECATED:
 * No uses VisualTransformation para mostrar placeholders.
 * Para Material3 usa el parámetro `placeholder` de TextField/OutlinedTextField,
 * y para BasicTextField dibuja el placeholder como otro composable cuando `text` está vacío.
 *
 * Esta transformación ahora es NO-OP para no interferir con el caret/selección.
 */
@Deprecated(
    message = "Usa el slot placeholder del TextField/OutlinedTextField o una decoración en BasicTextField.",
    replaceWith = ReplaceWith("/* Usa placeholder nativo */")
)
class PlaceholderTransformation(
    @Suppress("unused")
    val placeholder: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Mantener el texto original y un mapeo de offsets identidad para evitar bugs de cursor/selección.
        return TransformedText(text, OffsetMapping.Identity)
    }
}

/**
 * Manteniendo la firma por compatibilidad.
 * Devuelve un TransformedText identidad (no altera el texto ni offsets).
 */
@Deprecated(
    message = "Usa el slot placeholder nativo o BasicTextField con decoración.",
    replaceWith = ReplaceWith("/* Usa placeholder nativo */")
)
fun placeholderFilter(text: AnnotatedString, placeholder: String): TransformedText {
    return TransformedText(text, OffsetMapping.Identity)
}