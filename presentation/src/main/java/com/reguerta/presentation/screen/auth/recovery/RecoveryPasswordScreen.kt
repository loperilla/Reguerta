package com.reguerta.presentation.screen.auth.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.BtnType
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.Orange
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_88
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_BODY
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_TITLE
import com.reguerta.presentation.ui.Text

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

    if (state.showSuccessDialog) {
        ReguertaAlertDialog(
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(SIZE_88)
                        .background(Orange.copy(alpha = 0.2F), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = Orange,
                        modifier = Modifier
                            .size(SIZE_48)
                    )
                }
            },
            onDismissRequest = {},
            text = {
                TextBody(
                    text = "",
                    textSize = TEXT_SIZE_DLG_BODY,
                    textColor = Text,
                    textAlignment = TextAlign.Center
                )
            },
            title = {
                TextTitle(
                    text = "Se ha enviado el correo con éxito",
                    textSize = TEXT_SIZE_DLG_TITLE,
                    textColor = Text,
                    textAlignment = TextAlign.Center
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_EXTRA_SMALL, vertical = PADDING_SMALL),
                    horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)
                ) {
                    ReguertaButton(
                        textButton = "Aceptar",
                        isSingleButton = false,
                        btnType = BtnType.ERROR,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.onEvent(RecoveryEvent.GoBack)
                        }
                    )
                }
            },
            dismissButton = { /* No se usa  */ }
        )
    }

    if (state.showFailureDialog) {
        ReguertaAlertDialog(
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(SIZE_88)
                        .background(Orange.copy(alpha = 0.2F), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = Orange,
                        modifier = Modifier
                            .size(SIZE_48)
                    )
                }
            },
            onDismissRequest = {},
            text = {
                TextBody(
                    text = "",
                    textSize = TEXT_SIZE_DLG_BODY,
                    textColor = Text,
                    textAlignment = TextAlign.Center
                )
            },
            title = {
                TextTitle(
                    text = "No se ha encontrado el correo solicitado",
                    textSize = TEXT_SIZE_DLG_TITLE,
                    textColor = Text,
                    textAlignment = TextAlign.Center
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_EXTRA_SMALL, vertical = PADDING_SMALL),
                    horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)
                ) {
                    ReguertaButton(
                        textButton = "Aceptar",
                        isSingleButton = false,
                        btnType = BtnType.ERROR,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.onEvent(RecoveryEvent.HideFailureDialog)
                        }
                    )
                }
            },
            dismissButton = { /* No se usa  */ }
        )
    }

    Screen {
        Scaffold(
            topBar = {
                ReguertaTopBar(
                    topBarText = "Introduce el email de registro",
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
