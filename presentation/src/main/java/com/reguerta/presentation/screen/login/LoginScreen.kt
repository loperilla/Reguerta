package com.reguerta.presentation.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaPasswordInput
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextTitle
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
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
                placeholderText = "Email",
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReguertaPasswordInput(
                text = state.passwordInput,
                onTextChange = { newInputValue ->
                    newEvent(LoginEvent.OnPasswordChanged(newInputValue))
                },
                placeholderText = "Password",
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login",
                fontSize = 20.sp
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