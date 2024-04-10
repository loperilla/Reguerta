package com.reguerta.presentation.screen.users.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.User
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SecondaryBackground
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 6/2/24 at 16:52
 * All rights reserved 2024
 */

@Composable
fun usersScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<UserScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            viewModel.onEvent(UserScreenEvent.LoadUsers)
        }
    }
    if (state.goOut) {
        navigateTo(Routes.HOME.ROOT.route)
        return
    }
    if (state.userToEdit.isNotEmpty()) {
        navigateTo(Routes.USERS.EDIT.createRoute(state.userToEdit))
        return
    }
    Screen {
        UserScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navigateTo
        )
    }
}


@Composable
fun UserScreen(
    state: UserScreenState,
    onEvent: (UserScreenEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    AnimatedVisibility(
        state.showAreYouSure
    ) {
        AreYouSureDeleteDialog(onEvent)
    }
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Regüertenses autorizados",
                navActionClick = { onEvent(UserScreenEvent.GoOut) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .background(
                        color = SecondaryBackground,
                        shape = RoundedCornerShape(topStart = PADDING_MEDIUM, topEnd = PADDING_MEDIUM)
                    )
                    .padding(PADDING_SMALL)
            ) {
                ReguertaButton(
                    "Autorizar nuevo usuario",
                    onClick = { navigateTo(Routes.USERS.ADD.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = PADDING_SMALL,
                            horizontal = PADDING_MEDIUM
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            if (!state.isLoading) {
                UserListScreen(
                    state.userList,
                    onEvent,
                    navigateTo
                )
            }
        }
    }
}

@Composable
private fun UserListScreen(
    userList: List<User>,
    onEvent: (UserScreenEvent) -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_SMALL)
    ) {
        items(
            count = userList.size
        ) {
            UserItem(
                user = userList[it],
                onEvent = onEvent,
                navigateTo
            )
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onEvent: (UserScreenEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    ReguertaCard(
        modifier = Modifier
            .padding(PADDING_SMALL)
            .fillMaxWidth(),
        content = {
            TextBody(
                text = user.fullName,
                textSize = TEXT_SIZE_LARGE,
                textColor = Text,
                modifier = Modifier
                    .padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
            )

            TextBody(
                text = user.email,
                textSize = TEXT_SIZE_LARGE,
                textColor = Text,
                modifier = Modifier
                    .padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
            )

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = PADDING_SMALL),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaCheckBox(
                    isChecked = user.isProducer,
                    onCheckedChange = {
                        onEvent(UserScreenEvent.ToggleProducer(user.id))
                    }
                )
                TextBody(
                    text = "Es productor",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text
                )
            }

            if (user.isProducer) {
                TextBody(
                    text = user.companyName,
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
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
                    isChecked = user.isAdmin,
                    onCheckedChange = {
                        onEvent(UserScreenEvent.ToggleAdmin(user.id))
                    }
                )
                TextBody(
                    text = "Es admin",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_SMALL),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaIconButton(
                    iconButton = Icons.Filled.Edit,
                    onClick = {
                        navigateTo(Routes.USERS.EDIT.createRoute(user.id))
                    },
                    contentColor = PrimaryColor
                )
                ReguertaIconButton(
                    iconButton = Icons.Filled.Delete,
                    onClick = {
                        onEvent(UserScreenEvent.ShowAreYouSureDialog(user.id))
                    },
                    contentColor = Color.Red
                )
            }
        }
    )
}

@Composable
private fun AreYouSureDeleteDialog(
    onEvent: (UserScreenEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "ExitApp",
                tint = PrimaryColor,
                modifier = Modifier
                    .size(SIZE_48)
            )
        },
        onDismissRequest = { onEvent(UserScreenEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar a un usuario autorizado. Esta acción no se podrá deshacer",
                textSize = TEXT_SIZE_SMALL,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = buildAnnotatedString {
                    append("Vas a eliminar a un regüertense\n")
                    append("¿Estás seguro?")
                },
                textSize = TEXT_SIZE_LARGE,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Aceptar",
                onClick = {
                    onEvent(UserScreenEvent.ConfirmDelete)
                }
            )
        },
        dismissButton = {
            InverseReguertaButton(
                textButton = "Cancelar",
                onClick = {
                    onEvent(UserScreenEvent.HideAreYouSureDialog)
                }
            )
        }
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserScreenPreview() {
    Screen {
        UserScreen(
            state = UserScreenState(
                isLoading = false,
                showAreYouSure = false,
                userList = listOf(
                    User(
                        id = "1",
                        name = "Manuel",
                        surname = "Lopera",
                        email = "pLd6u@example.com",
                        companyName = "Reguerta",
                        isAdmin = true,
                        isProducer = true,
                        phone = "123456789",
                        numResignations = 0,
                        typeConsumer = "normal",
                        typeProducer = "",
                        available = true
                    )
                )
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}