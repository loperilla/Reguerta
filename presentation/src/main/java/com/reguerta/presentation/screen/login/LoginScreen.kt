package com.reguerta.presentation.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:43
 * All rights reserved 2024
 */

@Composable
fun loginScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.goOut) {
        navigateTo(Routes.HOME.route)
        return
    }

    Screen {
        LoginScreen(
            state = state,
            newEvent = viewModel::onEvent,
            navigateTo = navigateTo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    state: LoginState,
    newEvent: (LoginEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    if (state.errorMessage.isNotEmpty()) {
        LaunchedEffect(state.errorMessage.isNotEmpty()) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = state.errorMessage
            )

            when (snackbarResult) {
                SnackbarResult.Dismissed -> newEvent(LoginEvent.SnackbarHide)
                SnackbarResult.ActionPerformed -> {}
            }
        }
    }
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    TextTitle(
                        text = "Introduce tus credenciales",
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    newEvent(LoginEvent.OnEmailChanged(newInputValue))
                },
                placeholderText = "Pulsa para escribir",
                imeAction = ImeAction.Next,
                isValidEmail = state.emailInput.isValidEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            ReguertaPasswordInput(
                text = state.passwordInput,
                onTextChange = { newInputValue ->
                    if (newInputValue.length <= 16) {
                        newEvent(LoginEvent.OnPasswordChanged(newInputValue))
                    }
                },
                placeholderText = "Pulsa para escribir",
                imeAction = ImeAction.Done,
                isValidPassword = state.passwordInput.isValidPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            ReguertaButton(
                textButton = "Iniciar sesión",
                enabledButton = state.enabledButton,
                onClick = {
                    newEvent(LoginEvent.OnLoginClick)
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    Screen {
        LoginScreen(
            state = LoginState(),
            newEvent = {},
            navigateTo = {}
        )
    }
}