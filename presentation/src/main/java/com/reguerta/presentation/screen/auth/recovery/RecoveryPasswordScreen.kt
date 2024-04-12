package com.reguerta.presentation.screen.auth.recovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.auth.recovery
 * Created By Manuel Lopera on 7/4/24 at 15:52
 * All rights reserved 2024
 */

@Composable
fun recoveryPasswordScreen(
    navigateTo: (route: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val viewModel = hiltViewModel<RecoveryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.goOut) {
        navigateTo(Routes.AUTH.LOGIN.route)
        return
    }

    Screen {
        Scaffold(
            topBar = {
                ReguertaTopBar(
                    topBarText = "Introduce tus credenciales",
                    navActionClick = {
                        navigateTo(Routes.AUTH.FIRST_SCREEN.route)
                    }
                )
            }
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
                    text = state.email,
                    onTextChange = { newInputValue ->
                        viewModel.onEvent(RecoveryEvent.EmailChanged(newInputValue))
                    },
                    placeholderText = "Pulsa para escribir",
                    imeAction = ImeAction.Next,
                    isValidEmail = state.email.isValidEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PADDING_SMALL)
                )


                ReguertaButton(
                    textButton = "Recuperar contraseña",
                    enabledButton = state.email.isValidEmail,
                    onClick = {
                        viewModel.onEvent(RecoveryEvent.SendEmail)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PADDING_SMALL)
                )
            }
        }
    }
}
