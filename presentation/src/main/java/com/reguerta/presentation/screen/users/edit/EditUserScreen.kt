package com.reguerta.presentation.screen.users.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.CustomPhoneNumberInput
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaEmailInput
import com.reguerta.presentation.composables.ReguertaFullButton
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.screen.users.add.AddUserEvent
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

@OptIn(ExperimentalMaterial3Api::class)
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
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.zero),//(PADDING_ZERO),
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimens.Spacing.md)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding()
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
                    .height(IntrinsicSize.Min)
                    .padding(top = Dimens.Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextTitle(
                    text = "TELÉFONO DEL REGÜERTENSE",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.Spacing.md)
                )
                Box(
                    modifier = Modifier
                        .widthIn(min = 80.dp, max = 120.dp)
                        .wrapContentHeight()
                        .align(Alignment.CenterVertically)
                ) {
                    CustomPhoneNumberInput(
                        value = state.phoneNumber,
                        onValueChange = { phoneNumberValue ->
                            if (phoneNumberValue.length <= 9) {
                                onEvent(EditUserEvent.PhoneNumberInputChanges(phoneNumberValue))
                            }
                        },
                        placeholder = "Teléfono",
                        isError = state.phoneNumber.length != 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
            }

            if (state.isProducer) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimens.Spacing.sm)
                        .wrapContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(
                        Dimens.Spacing.sm,
                        Alignment.End
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextBody(
                        text = "Consumidor encargado de compras",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ReguertaCheckBox(
                        isChecked = state.typeProducer == "compras",
                        onCheckedChange = { newValue ->
                            onEvent(EditUserEvent.ToggledIsShoppingProducer(newValue))
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(start = Dimens.Spacing.sm, top = Dimens.Spacing.md)
                    .wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimens.Spacing.sm,
                    Alignment.Start
                ),
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .padding(start = Dimens.Spacing.sm)
                    .wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimens.Spacing.sm,
                    Alignment.Start
                ),
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(Dimens.Spacing.xl))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ReguertaFullButton(
                    textButton = "Actualizar",
                    enabled = state.isButtonEnabled,
                    onClick = { onEvent(EditUserEvent.EditUser) }
                )
            }
            Spacer(modifier = Modifier.height(Dimens.Spacing.lg))
        }
    }
}
