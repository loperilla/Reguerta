package com.reguerta.presentation.screen.users.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.CustomPhoneNumberInput
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PADDING_ZERO
import com.reguerta.presentation.ui.TEXT_SIZE_EXTRA_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM

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
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Autorizar regüertense",
                navActionClick = { onEvent(EditUserEvent.GoBack) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_ZERO),
            modifier = Modifier
                .padding(it)
                .padding(horizontal = PADDING_MEDIUM)
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
                    .padding(top = PADDING_SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextTitle(
                    text = "TELÉFONO DEL REGÜERTENSE",
                    textSize = TEXT_SIZE_EXTRA_SMALL,
                    modifier = Modifier
                        .weight(0.64f)
                        .padding(start = PADDING_MEDIUM)
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
                    horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
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
                        textSize = TEXT_SIZE_MEDIUM,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
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
                    textSize = TEXT_SIZE_LARGE,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
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
                    textSize = TEXT_SIZE_LARGE,
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
                    .padding(PADDING_SMALL)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
