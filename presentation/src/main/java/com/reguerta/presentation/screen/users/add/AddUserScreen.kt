package com.reguerta.presentation.screen.users.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.PhoneNumberReguertaInput
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_user
 * Created By Manuel Lopera on 17/2/24 at 11:57
 * All rights reserved 2024
 */

@Composable
fun addUserScreen(
    popBack: () -> Unit
) {
    val viewModel = hiltViewModel<AddUserViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        popBack()
        return
    }
    Screen {
        AddUserScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun AddUserScreen(
    state: AddUserState,
    onEvent: (AddUserEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Autorizar regüertense",
                navActionClick = { onEvent(AddUserEvent.GoBack) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            modifier = Modifier.padding(it)
        ) {
            ReguertaEmailInput(
                state.email,
                onTextChange = { emailValue ->
                    onEvent(AddUserEvent.EmailInputChanges(emailValue))
                },
                labelText = "Email",
                isValidEmail = state.email.isValidEmail,
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PADDING_MEDIUM, end = PADDING_MEDIUM)
            )

            TextReguertaInput(
                state.name,
                onTextChange = { nameValue ->
                    onEvent(AddUserEvent.NameInputChanges(nameValue))
                },
                labelText = "Nombre",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PADDING_MEDIUM, end = PADDING_MEDIUM)
            )

            TextReguertaInput(
                state.surname,
                onTextChange = { surnameValue ->
                    onEvent(AddUserEvent.SurnameInputChanges(surnameValue))
                },
                labelText = "Apellidos",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PADDING_MEDIUM, end = PADDING_MEDIUM)
            )

            PhoneNumberReguertaInput(
                state.phoneNumber,
                onTextChange = { phoneNumberValue ->
                    if (phoneNumberValue.length <= 9) {
                        onEvent(AddUserEvent.PhoneNumberInputChanges(phoneNumberValue))
                    }
                },
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PADDING_MEDIUM, end = PADDING_MEDIUM)
            )

            if (state.isProducer) {
                TextReguertaInput(
                    state.companyName,
                    onTextChange = { companyNameValue ->
                        onEvent(AddUserEvent.CompanyNameInputChanges(companyNameValue))
                    },
                    labelText = "Compañía",
                    placeholderText = "Pulsa para escribir",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = PADDING_MEDIUM, end = PADDING_MEDIUM)
                )
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = PADDING_SMALL),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaCheckBox(
                    isChecked = state.isProducer,
                    onCheckedChange = { newValue ->
                        onEvent(AddUserEvent.ToggledIsProducer(newValue))
                    }
                )
                TextBody(
                    text = "Es productor",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text
                )
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = PADDING_SMALL),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaCheckBox(
                    isChecked = state.isAdmin,
                    onCheckedChange = { newValue ->
                        onEvent(AddUserEvent.ToggledIsAdmin(newValue))
                    }
                )
                TextBody(
                    text = "Es administrador",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text
                )
            }

            ReguertaButton(
                textButton = "Autorizar",
                onClick = { onEvent(AddUserEvent.AddUser) },
                enabledButton = state.isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_MEDIUM)
            )
        }
    }
}

@Preview
@Composable
fun AddUserScreenPreview() {
    Screen {
        AddUserScreen(
            state = AddUserState(
                isProducer = true
            ),
            onEvent = {}
        )
    }
}