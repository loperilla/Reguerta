package com.reguerta.presentation.screen.auth.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.BtnType
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.navigation.Routes

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
                        .size(Dimens.Size.dp88)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(Dimens.Size.dp48)
                    )
                }
            },
            onDismissRequest = {},
            text = {
                TextBody(
                    text = "Se ha enviado el correo de restablecimiento de contraseña con éxito. Revisa tu correo.",
                    textSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center
                )
            },
            title = {
                TextTitle(
                    text = "Recuperar contraseña",
                    textSize = MaterialTheme.typography.titleLarge.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.xs, vertical = Dimens.Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
                ) {
                    ReguertaButton(
                        textButton = "Aceptar",
                        isSingleButton = false,
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
                        .size(Dimens.Size.dp88)
                        .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Advertencia",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(Dimens.Size.dp48)
                    )
                }
            },
            onDismissRequest = {},
            text = {
                TextBody(
                    text = "Ha ocurrido un error al enviar el correo de restablecimiento de contraseña.",
                    textSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center
                )
            },
            title = {
                TextTitle(
                    text = "Recuperar contraseña",
                    textSize = MaterialTheme.typography.titleLarge.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.xs, vertical = Dimens.Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
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
        ReguertaScaffold(
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
                verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm),
                modifier = Modifier
                    .padding(it)
                    .padding(Dimens.Spacing.md)
                    .fillMaxSize()
                    .imePadding()
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
                        .padding(Dimens.Spacing.sm)
                )
                Spacer(
                    modifier = Modifier.size(Dimens.Spacing.lg)
                )

                ReguertaButton(
                    textButton = "Recuperar contraseña",
                    enabledButton = state.email.isValidEmail,
                    onClick = {
                        viewModel.onEvent(RecoveryEvent.SendEmail)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.Spacing.sm)
                )
            }
        }
    }
}
