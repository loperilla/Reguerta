package com.reguerta.presentation.screen.users.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.CustomPhoneNumberInput
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.Dimens

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_user
 * Created By Manuel Lopera on 24/2/24 at 13:02
 * All rights reserved 2024
 */

@Composable
fun editUserScreen(id: String, navigateTo: () -> Unit) {
    val viewModel = hiltViewModel<EditUserViewModel, EditUserViewModelFactory> { factory ->
        factory.create(id)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo()
        return
    }
    Screen {
        EditUserScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun EditUserScreen(
    state: EditUserState,
    onEvent: (EditUserEvent) -> Unit
) {
    ReguertaScaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Autorizar regüertense",
                navActionClick = { onEvent(EditUserEvent.GoBack) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.zero),
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimens.Spacing.md)
        ) {
            ReguertaEmailInput(
                state.email,
                onTextChange = { emailValue ->
                    onEvent(EditUserEvent.EmailInputChanges(emailValue))
                },
                labelText = "EMAIL A AUTORIZAR",
                isValidEmail = state.email.isValidEmail,
                placeholderText = "Pulsa para escribir",
                modifier = Modifier.fillMaxWidth()
            )

            TextReguertaInput(
                state.name,
                onTextChange = { nameValue ->
                    onEvent(EditUserEvent.NameInputChanges(nameValue))
                },
                labelText = "NOMBRE DEL REGÜERTENSE",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier.fillMaxWidth()
            )

            TextReguertaInput(
                state.surname,
                onTextChange = { surnameValue ->
                    onEvent(EditUserEvent.SurnameInputChanges(surnameValue))
                },
                labelText = "APELLIDOS DEL REGÜERTENSE",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextTitle(
                    text = "TELÉFONO DEL REGÜERTENSE",
                    textSize = MaterialTheme.typography.titleMedium.fontSize,
                    modifier = Modifier
                        .weight(0.64f)
                        .padding(start = Dimens.Spacing.md)
                )
                CustomPhoneNumberInput(
                    value = state.phoneNumber,
                    onValueChange = { phoneNumberValue ->
                        if (phoneNumberValue.length <= 9) {
                            onEvent(EditUserEvent.PhoneNumberInputChanges(phoneNumberValue))
                        }
                    },
                    placeholder = "Teléfono",
                    isError = state.phoneNumber.length != 9,
                    modifier = Modifier.weight(0.36f)
                )
            }

            if (state.isProducer) {
                TextReguertaInput(
                    state.companyName,
                    onTextChange = { companyNameValue ->
                        onEvent(EditUserEvent.CompanyNameInputChanges(companyNameValue))
                    },
                    labelText = "NOMBRE DE LA EMPRESA",
                    placeholderText = "Pulsa para escribir",
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.xs, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReguertaCheckBox(
                        isChecked = state.typeProducer == "compras",
                        onCheckedChange = { newValue ->
                            onEvent(EditUserEvent.ToggledIsShoppingProducer(newValue))
                        }
                    )
                    TextBody(
                        text = "Consumidor encargado de compras",
                        textSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.xs, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaCheckBox(
                    isChecked = state.isProducer,
                    onCheckedChange = { newValue ->
                        onEvent(EditUserEvent.ToggledIsProducer(newValue))
                    }
                )
                TextBody(
                    text = "Es productor",
                    textSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.xs, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaCheckBox(
                    isChecked = state.isAdmin,
                    onCheckedChange = { newValue ->
                        onEvent(EditUserEvent.ToggledIsAdmin(newValue))
                    }
                )
                TextBody(
                    text = "Es administrador",
                    textSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            ReguertaButton(
                textButton = "Actualizar",
                onClick = { onEvent(EditUserEvent.EditUser) },
                enabledButton = state.isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Spacing.sm)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
