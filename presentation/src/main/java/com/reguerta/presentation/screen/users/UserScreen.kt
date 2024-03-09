package com.reguerta.presentation.screen.users

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.User
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SecondaryBackground
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

@OptIn(ExperimentalMaterial3Api::class)
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
            MediumTopAppBar(
                title = {
                    TextTitle(
                        text = "Regüertenses autorizados",
                        textSize = 26.sp,
                        textColor = Text
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(UserScreenEvent.GoOut) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .background(
                        color = SecondaryBackground,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(8.dp)
            ) {
                ReguertaButton(
                    "Autorizar nuevo usuario",
                    onClick = { navigateTo(Routes.USERS.ADD.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
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
            .padding(8.dp)
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
            .padding(8.dp)
            .fillMaxWidth(),
        content = {
            TextBody(
                text = user.fullName,
                textSize = 18.sp,
                textColor = Text,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
            )

            TextBody(
                text = user.email,
                textSize = 16.sp,
                textColor = Text,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
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
                    textSize = 16.sp,
                    textColor = Text
                )
            }

            if (user.isProducer) {
                TextBody(
                    text = user.companyName,
                    textSize = 14.sp,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp)
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
                    isChecked = user.isAdmin,
                    onCheckedChange = {
                        onEvent(UserScreenEvent.ToggleAdmin(user.id))
                    }
                )
                TextBody(
                    text = "Es admin",
                    textSize = 16.sp,
                    textColor = Text
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
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
                    .size(48.dp)
            )
        },
        onDismissRequest = { onEvent(UserScreenEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar a un usuario autorizado. Esta acción no se podrá deshacer",
                textSize = 14.sp,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Vas a eliminar a un regüertense/n ¿Estás seguro?",
                textSize = 18.sp,
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
                showAreYouSure = true,
                userList = listOf(
                    User(
                        id = "1",
                        name = "Manuel",
                        surname = "Lopera",
                        email = "pLd6u@example.com",
                        companyName = "Reguerta",
                        isAdmin = true,
                        isProducer = true
                    )
                )
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}