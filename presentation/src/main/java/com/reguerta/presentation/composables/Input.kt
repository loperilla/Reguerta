package com.reguerta.presentation.composables

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.reguerta.presentation.composables.input.PlaceholderTransformation
import com.reguerta.presentation.composables.input.UiError
import com.reguerta.presentation.ui.BORDER_SIZE
import com.reguerta.presentation.ui.CORNER_SIZE_8
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.SIZE_40
import com.reguerta.presentation.ui.TEXT_SIZE_EXTRA_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
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
fun PhoneNumberReguertaInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
) {
    SecondaryReguertaInput(
        text = text,
        uiError = UiError("Este teléfono no es válido", text.length == 9),
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = keyboardType,
        imeAction = imeAction,
        modifier = modifier,
    )
}

@Composable
fun SecondaryTextReguertaInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    SecondaryReguertaInput(
        text = text,
        uiError = UiError("Este campo no puede estar vacío", text.isNotEmpty()),
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = keyboardType,
        imeAction = imeAction,
        modifier = modifier,
    )
}

@Composable
private fun SecondaryReguertaInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    uiError: UiError = UiError(),
    placeholderText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
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
        focusedBorderColor = PrimaryColor,
        unfocusedBorderColor = PrimaryColor,
        disabledPlaceholderColor = Text,
        focusedPlaceholderColor = Text,
        errorPlaceholderColor = Text,
        unfocusedPlaceholderColor = Text.copy(alpha = 0.7f),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        errorContainerColor = Color.White,
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            colors = colors,
            isError = text.isNotEmpty() && !uiError.isVisible,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = cabinsketchFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = TEXT_SIZE_LARGE
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
                textSize = TEXT_SIZE_SMALL,
                textColor = Color.Red,
                modifier = Modifier
                    .padding(horizontal = PADDING_MEDIUM)
            )
        }
    }
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
        verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            colors = colors,
            isError = text.isNotEmpty() && !uiError.isVisible,
            singleLine = true,
            trailingIcon = trailingIcon,
            label = {
                TextTitle(
                    text = labelText,
                    textSize = TEXT_SIZE_EXTRA_SMALL
                )
            },
            textStyle = TextStyle(
                fontFamily = cabinsketchFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = TEXT_SIZE_LARGE
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
            suffix = {
                if (suffixValue.isNotEmpty()) {
                    TextBody(
                        text = suffixValue,
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text
                    )
                }
            },
            modifier = modifier
        )

        if (text.isNotEmpty() && !uiError.isVisible) {
            TextBody(
                text = uiError.message,
                textSize = TEXT_SIZE_SMALL,
                textColor = Color.Red,
                modifier = Modifier.padding(horizontal = PADDING_MEDIUM)
            )
        }
    }
}



@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    uiError: UiError = UiError(),
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default
) {
    val textStyle = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = TEXT_SIZE_LARGE,
        textAlign = TextAlign.Center
    )

    Box(
        modifier = modifier
            .height(SIZE_40)
            .background(Color.White, RoundedCornerShape(CORNER_SIZE_8))
            .border(
                BORDER_SIZE,
                if (isError) Color.Red else PrimaryColor,
                RoundedCornerShape(CORNER_SIZE_8)
            )
            .padding(PADDING_SMALL)
    ) {
        if (value.isEmpty()) {
            TextBody(
                text = placeholder,
                textSize = TEXT_SIZE_LARGE,
                textColor = Text.copy(alpha = 0.5f),
                textAlignment = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle.copy(color = Text),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun CustomTextFieldWithLabel(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL),
        modifier = modifier
    ) {
        TextTitle(
            text = label.uppercase(),
            textColor = Text,
            textSize = TEXT_SIZE_SMALL
        )
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            isError = isError,
            keyboardType = keyboardType,
            imeAction = imeAction,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaInputPreview() {
    Screen {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            modifier = Modifier
                .padding(PADDING_SMALL)
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

            SecondaryReguertaInput(
                text = "Manuel Lopera",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )

            PhoneNumberReguertaInput(
                text = "123456789",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
            PhoneNumberReguertaInput(
                text = "12345679",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}