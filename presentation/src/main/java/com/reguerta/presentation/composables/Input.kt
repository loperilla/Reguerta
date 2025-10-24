package com.reguerta.presentation.composables

import androidx.compose.ui.focus.onFocusChanged

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.reguerta.presentation.state.hasError
import com.reguerta.presentation.state.UiError
import androidx.compose.foundation.layout.heightIn
import com.reguerta.presentation.ui.Dimens
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp

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
    labelText: String = "EMAIL",
    imeAction: ImeAction = ImeAction.Default
) {
    ReguertaInput(
        text = text,
        labelText = labelText,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Email,
        uiError = UiError("Ingresa un formato de email válido", isValidEmail),
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
    labelText: String = "CONTRASEÑA",
    imeAction: ImeAction = ImeAction.Default
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    ReguertaInput(
        text = text,
        labelText = labelText,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Password,
        uiError = UiError("Ingrese una contraseña válida (6-16 caracteres)", isValidPassword),
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
                Icon(
                    imageVector = endIcon,
                    contentDescription = "show or hide password",
                    modifier = Modifier.size(Dimens.Components.Input.trailingIconSize)
                )
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
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffixValue: String = ""
) {
    ReguertaInput(
        text = text,
        labelText = labelText,
        uiError = UiError("Este campo no puede estar vacío", text.isNotEmpty()),
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = keyboardType,
        imeAction = imeAction,
        modifier = modifier,
        suffixValue = suffixValue
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
    suffixValue: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val textColor = if (text.isEmpty()) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val colors = TextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.38f),
        errorTextColor = MaterialTheme.colorScheme.error,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        errorContainerColor = MaterialTheme.colorScheme.background,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        errorPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
    var touched by rememberSaveable { mutableStateOf(false) }
    val showError = text.isNotEmpty() && uiError.hasError && touched
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            colors = colors,
            isError = showError,
            singleLine = true,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(Dimens.Components.Input.cornerRadius),
            label = {
                TextBody(
                    text = labelText,
                    style = Dimens.Components.Input.labelStyle
                )
            },
            textStyle = Dimens.Components.Input.textStyle,
            placeholder = {
                if (text.isEmpty() && placeholderText.isNotEmpty()) {
                    TextBody(
                        text = placeholderText,
                        style = Dimens.Components.Input.textStyle,
                        textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            suffix = {
                if (suffixValue.isNotEmpty()) {
                    TextBody(
                        text = suffixValue,
                        style = Dimens.Components.Input.textStyle,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            supportingText = {
                if (showError) {
                    TextBody(
                        text = uiError.message,
                        style = Dimens.Components.Input.supportingStyle,
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = Dimens.Spacing.xs)
                    )
                }
            },
            modifier = modifier.heightIn(min = Dimens.Components.Input.minHeight).onFocusChanged { if (!it.isFocused) touched = true }
        )
    }
}

@Composable
fun CustomPhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    imeAction: ImeAction = ImeAction.Default,
    supportingText: String = ""
) {
    val textStyle = Dimens.Components.Input.textStyle.copy(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    var touched by rememberSaveable { mutableStateOf(false) }
    val showSupporting = isError && touched && supportingText.isNotEmpty()
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
    ) {
        Box(
            modifier = modifier
                .heightIn(min = Dimens.Size.dp32)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(Dimens.Components.Input.cornerRadius))
                .border(
                    Dimens.Border.thin,
                    if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(Dimens.Components.Input.cornerRadius)
                )
                .padding(Dimens.Spacing.sm)
                .onFocusChanged { if (!it.isFocused) touched = true }
        ) {
            if (value.isEmpty()) {
                TextBody(
                    text = placeholder,
                    style = Dimens.Components.Input.textStyle.copy(textAlign = TextAlign.Center),
                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = imeAction
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
        }
        if (showSupporting) {
            TextBody(
                text = supportingText,
                style = Dimens.Components.Input.supportingStyle,
                textColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = Dimens.Spacing.md)
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    supportingText: String = "",
    minHeight: Dp = Dimens.Components.Input.minHeight
) {
    val textStyle = Dimens.Components.Input.textStyle.copy(
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    var touched by rememberSaveable { mutableStateOf(false) }
    val showSupporting = isError && touched && supportingText.isNotEmpty()
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
    ) {
        Box(
            modifier = modifier
                .heightIn(min = minHeight)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(Dimens.Components.Input.cornerRadius))
                .border(
                    Dimens.Border.thin,
                    if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(Dimens.Components.Input.cornerRadius)
                )
                .padding(Dimens.Spacing.sm)
                .onFocusChanged { if (!it.isFocused) touched = true }
        ) {
            if (value.isEmpty()) {
                TextBody(
                    text = placeholder,
                    style = Dimens.Components.Input.textStyle.copy(textAlign = TextAlign.Center),
                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
        }
        if (showSupporting) {
            TextBody(
                text = supportingText,
                style = Dimens.Components.Input.supportingStyle,
                textColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = Dimens.Spacing.md)
            )
        }
    }
}
