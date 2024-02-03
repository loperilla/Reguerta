package com.reguerta.presentation.screen.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaPasswordInput
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.type.isValidPassword
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.register
 * Created By Manuel Lopera on 30/1/24 at 20:09
 * All rights reserved 2024
 */

@Composable
fun registerScreen(navigateTo: (String) -> Unit) {
    val viewModel = hiltViewModel<RegisterViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.goOut) {
        navigateTo(Routes.HOME.route)
        return
    }

    Screen {
        RegisterScreen(
            state = state,
            newEvent = viewModel::onEvent,
            navigateTo = navigateTo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    state: RegisterState,
    newEvent: (RegisterEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    TextTitle(
                        text = "Regístrate",
                        textSize = 26.sp,
                        textColor = PrimaryColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.AUTH.FIRST_SCREEN.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ReguertaEmailInput(
                text = state.emailInput,
                onTextChange = { newInputValue ->
                    newEvent(RegisterEvent.OnEmailChanged(newInputValue))
                },
                placeholderText = "Pulsa para escribir",
                isValidEmail = state.emailInput.isValidEmail,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            ReguertaPasswordInput(
                text = state.passwordInput,
                onTextChange = { newInputValue ->
                    if (newInputValue.length >= 6) {
                        newEvent(RegisterEvent.OnPasswordChanged(newInputValue))
                    }
                },
                placeholderText = "Pulsa para escribir",
                isValidPassword = state.passwordInput.isValidPassword,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            ReguertaPasswordInput(
                text = state.repeatPasswordInput,
                onTextChange = { newInputValue ->
                    if (newInputValue.length >= 6) {
                        newEvent(RegisterEvent.OnRepeatPasswordChanged(newInputValue))
                    }
                },
                placeholderText = "Pulsa para escribir",
                isValidPassword = state.repeatPasswordInput.isValidPassword,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReguertaButton(
                textButton = "Registrarse",
                enabledButton = state.enabledButton,
                onClick = {
                    newEvent(RegisterEvent.OnRegisterClick)
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    Screen {
        RegisterScreen(
            state = RegisterState(),
            newEvent = {},
            navigateTo = {}
        )
    }
}
