package com.reguerta.presentation.screen.auth.recovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ButtonLayout
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaFullButton
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.navigation.Routes
import com.reguerta.domain.enums.UiType

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.auth.recovery
 * Created By Manuel Lopera on 7/4/24 at 15:52
 * All rights reserved 2024
 */

@OptIn(ExperimentalMaterial3Api::class)
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
            onDismissRequest = {},
            icon = Icons.Default.Info,
            titleText = "Recuperar contraseña",
            bodyText = "Se ha enviado el correo de restablecimiento de contraseña con éxito. Revisa tu correo.",
            confirmText = "Aceptar",
            onConfirm = { viewModel.onEvent(RecoveryEvent.GoBack) },
            type = UiType.INFO
        )
    }

    if (state.showFailureDialog) {
        ReguertaAlertDialog(
            onDismissRequest = {},
            icon = Icons.Default.Warning,
            titleText = "Recuperar contraseña",
            bodyText = "Ha ocurrido un error al enviar el correo de restablecimiento de contraseña.",
            confirmText = "Aceptar",
            onConfirm = { viewModel.onEvent(RecoveryEvent.HideFailureDialog) },
            type = UiType.ERROR
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

                ReguertaFullButton(
                    textButton = "Recuperar contraseña",
                    enabled = state.email.isValidEmail,
                    onClick = {
                        viewModel.onEvent(RecoveryEvent.SendEmail)
                        keyboardController?.hide()
                    },
                    layout = ButtonLayout.Fill,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.Spacing.sm)
                )
            }
        }
    }
}
