package com.reguerta.presentation.screen.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaPasswordInput
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.type.isValidPassword
import com.reguerta.presentation.ui.PADDING_EXTRA_LARGE
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL

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

    LaunchedEffect(Unit) {
        viewModel.autoLoginIfDebug()
    }

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
            ReguertaTopBar(
                topBarText = "Introduce tus credenciales",
                navActionClick = {
                    navigateTo(Routes.AUTH.FIRST_SCREEN.route)
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            modifier = Modifier
                .padding(it)
                .padding(PADDING_MEDIUM)
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
                    .padding(PADDING_SMALL)
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
                    .padding(PADDING_SMALL)
            )

            TextBody(
                textColor = PrimaryColor,
                text = "¿Has olvidado tu contraseña?",
                textSize = TEXT_SIZE_SMALL,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(PADDING_SMALL)
                    .clickable {
                        navigateTo(Routes.AUTH.RECOVERY_PASSWORD.route)
                    }
            )
            Spacer(modifier = Modifier.height(PADDING_EXTRA_LARGE))
            ReguertaButton(
                textButton = "Iniciar sesión",
                enabledButton = state.enabledButton,
                onClick = {
                    newEvent(LoginEvent.OnLoginClick)
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_SMALL)
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