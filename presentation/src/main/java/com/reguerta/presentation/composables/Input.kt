package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    imeAction: ImeAction = ImeAction.Default
) {
    ReguertaInput(
        text = text,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Email,
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
    imeAction: ImeAction = ImeAction.Default
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    ReguertaInput(
        text = text,
        onTextChange = onTextChange,
        placeholderText = placeholderText,
        keyboardType = KeyboardType.Password,
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
private fun ReguertaInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Text,
        unfocusedTextColor = Text.copy(
            alpha = 0.7f
        ),
        focusedBorderColor = Text,
        unfocusedBorderColor = Text.copy(
            alpha = 0.7f
        ),
        disabledPlaceholderColor = Text,
        focusedPlaceholderColor = Text,
        errorPlaceholderColor = Text,
        unfocusedPlaceholderColor = Text.copy(
            alpha = 0.7f
        ),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        errorContainerColor = Color.White
    )
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        colors = colors,
        trailingIcon = trailingIcon,
        textStyle = TextStyle(
            fontFamily = cabinsketchFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        ),
        visualTransformation = visualTransformation,
        placeholder = {
            TextBody(
                placeholderText,
                textSize = 14.sp
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaInputPreview() {
    Screen {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            ReguertaEmailInput(
                text = "Manuel Lopera",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            ReguertaPasswordInput(
                text = "",
                placeholderText = "Manuel Lopera",
                onTextChange = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}