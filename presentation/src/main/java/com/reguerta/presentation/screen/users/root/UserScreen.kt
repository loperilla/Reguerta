package com.reguerta.presentation.screen.users.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.User
import com.reguerta.presentation.composables.BtnType
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.screen.products.root.ProductsEvent
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_88
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_BODY
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_TITLE
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM

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
    AnimatedVisibility(state.showAreYouSure) {
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
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
            )

            TextBody(
                text = user.email,
                textSize = TEXT_SIZE_LARGE,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
            )

            if (user.isProducer) {
                TextBody(
                    text = "Es productor. ${user.companyName}",
                    textSize = TEXT_SIZE_MEDIUM,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
                )
            }
            if (user.isAdmin) {
                TextBody(
                    text = "Es administrador",
                    textSize = TEXT_SIZE_MEDIUM,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
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
                    contentColor = MaterialTheme.colorScheme.primary
                )
                ReguertaIconButton(
                    iconButton = Icons.Filled.Delete,
                    onClick = {
                        onEvent(UserScreenEvent.ShowAreYouSureDialog(user.id))
                    },
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}


@Composable
private fun AreYouSureDialog(
    onEvent: (ProductsEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(ProductsEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar un producto.\nEsta acción no se podrá deshacer.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Vas a eliminar un producto\n¿Estás seguro?",
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
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
                InverseReguertaButton(
                    textButton = "Cancelar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(ProductsEvent.HideAreYouSureDialog)
                    }
                )
                ReguertaButton(
                    textButton = "Aceptar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(ProductsEvent.ConfirmDeleteProduct)
                    }
                )
            }
        },
        dismissButton = { /* No se usa  */ }
    )
}

@Composable
private fun AreYouSureDeleteDialog(
    onEvent: (UserScreenEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(UserScreenEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Este usuario no podrá entrar en la app.\nEsta acción no se podrá deshacer.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = buildAnnotatedString {
                    append("Vas a eiminar regüertense\n")
                    append("¿Estás seguro?")
                },
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
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
                InverseReguertaButton(
                    textButton = "Cancelar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(UserScreenEvent.HideAreYouSureDialog)
                    }
                )
                ReguertaButton(
                    textButton = "Aceptar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(UserScreenEvent.ConfirmDelete)
                    }
                )
            }
        },
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
                        available = true,
                        tropical1 = 0.0,
                        tropical2 = 0.0
                    )
                )
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}