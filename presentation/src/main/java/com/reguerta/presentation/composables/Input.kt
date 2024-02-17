package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.composables.input.PlaceholderTransformation
import com.reguerta.presentation.composables.input.UiError
import com.reguerta.presentation.ui.Text
import com.reguerta.presentation.ui.cabinsketchFontFamily

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 19:43
 * All rights reserved 2024
 */

@Composable
fun ReguertaEmailInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    isValidEmail: Boolean = true,
    labelText: String = "Email",
    imeAction: ImeAction = ImeAction.Default
) {
    ReguertaInput(
        text = text,
        labelText = labelText,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Email,
        uiError = UiError("Email no válido", isValidEmail),
        imeAction = imeAction,
        modifier = modifier
    )
}

@Composable
fun ReguertaPasswordInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    isValidPassword: Boolean = true,
    labelText: String = "Contraseña",
    imeAction: ImeAction = ImeAction.Default
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    ReguertaInput(
        text = text,
        labelText = labelText,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Password,
        uiError = UiError("Ingrese una contraseña válida (6-16 caracteres)", isValidPassword),
        imeAction = imeAction,
        trailingIcon = {
            val endIcon = if (isPasswordVisible) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible }
            ) {
                Icon(imageVector = endIcon, contentDescription = "show or hide password")
            }
        },
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        modifier = modifier
    )
}

@Composable
fun TextReguertaInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    labelText: String = "",
    imeAction: ImeAction = ImeAction.Default
) {
    ReguertaInput(
        text = text,
        labelText = labelText,
        uiError = UiError("Este campo no puede estar vacío", text.isNotEmpty()),
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Text,
        imeAction = imeAction,
        modifier = modifier
    )
}

@Composable
private fun ReguertaInput(
    text: String,
    labelText: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiError: UiError = UiError(),
    placeholderText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val textColor = if (text.isEmpty()) {
        Text.copy(alpha = 0.7f)
    } else {
        Text
    }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedBorderColor = Text,
        unfocusedBorderColor = Text.copy(
            alpha = 0.7f
        ),
        disabledPlaceholderColor = Text,
        focusedPlaceholderColor = Text,
        errorPlaceholderColor = Text,
        unfocusedPlaceholderColor = Text.copy(
            alpha = 0.7f
        )
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            colors = colors,
            isError = text.isNotEmpty() && !uiError.isVisible,
            singleLine = true,
            trailingIcon = trailingIcon,
            label = {
                TextBody(
                    text = labelText,
                    textSize = 12.sp
                )
            },
            textStyle = TextStyle(
                fontFamily = cabinsketchFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            visualTransformation = if (text.isEmpty()) {
                PlaceholderTransformation(placeholderText)
            } else {
                visualTransformation
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            modifier = modifier
        )

        if (text.isNotEmpty() && !uiError.isVisible) {
            TextBody(
                text = uiError.message,
                textSize = 12.sp,
                textColor = Color.Red,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaInputPreview() {
    Screen {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            ReguertaEmailInput(
                text = "Manuel Lopera",
                onTextChange = {},
                isValidEmail = false,
                modifier = Modifier
                    .fillMaxWidth()
            )

            ReguertaPasswordInput(
                text = "",
                placeholderText = "Contraseña",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )

            TextReguertaInput(
                text = "Manuel Lopera",
                labelText = "Nombre",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}