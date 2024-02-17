package com.reguerta.presentation.screen.add_user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.type.isValidEmail
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    state: AddUserState,
    onEvent: (AddUserEvent) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    TextTitle(
                        "Autorizar reguertense",
                        textSize = 26.sp,
                        textColor = Text
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddUserEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    .padding(start = 16.dp, end = 16.dp)
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
                    .padding(start = 16.dp, end = 16.dp)
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
                    .padding(start = 16.dp, end = 16.dp)
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
                        .padding(start = 16.dp, end = 16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
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
                    textSize = 16.sp,
                    textColor = Text
                )
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
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
                    textSize = 16.sp,
                    textColor = Text
                )
            }

            ReguertaButton(
                textButton = "Autorizar",
                onClick = { onEvent(AddUserEvent.AddUser) },
                enabledButton = state.isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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