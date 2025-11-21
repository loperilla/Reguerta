package com.reguerta.presentation.screen.users.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.reguerta.presentation.composables.ReguertaScaffold
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.User
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaFullButton
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.navigation.Routes
import com.reguerta.presentation.screen.users.add.AddUserEvent
import com.reguerta.presentation.ui.Dimens
import com.reguerta.domain.enums.UiType
import com.reguerta.presentation.composables.ReguertaAlertDialog

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
    AnimatedVisibility(state.showAreYouSure) {
        AreYouSureDeleteDialog(onEvent)
    }
    ReguertaScaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Regüertenses autorizados",
                navActionClick = { onEvent(UserScreenEvent.GoOut) }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = Dimens.Radius.lg, topEnd = Dimens.Radius.lg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Dimens.Spacing.sm, vertical = Dimens.Spacing.sm)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ReguertaFullButton(
                            "Autorizar regüertense",
                            onClick = { navigateTo(Routes.USERS.ADD.route) }
                        )
                    }
                }
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
            .padding(Dimens.Spacing.sm)
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
            .padding(Dimens.Spacing.sm)
            .fillMaxWidth(),
        content = {
            TextBody(
                text = user.fullName,
                textSize = MaterialTheme.typography.bodyLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.sm)
            )

            TextBody(
                text = user.email,
                textSize = MaterialTheme.typography.bodyLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.sm)
            )

            if (user.isProducer) {
                TextBody(
                    text = "Es productor. ${user.companyName}",
                    textSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.sm)
                )
            }
            if (user.isAdmin) {
                TextBody(
                    text = "Es administrador",
                    textSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.sm)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.xs, Alignment.End),
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
private fun AreYouSureDeleteDialog(
    onEvent: (UserScreenEvent) -> Unit
) {
    ReguertaAlertDialog(
        onDismissRequest = { onEvent(UserScreenEvent.HideAreYouSureDialog) },
        icon = Icons.Filled.Warning,
        titleText = "Vas a eliminar regüertense\n¿Estás seguro?",
        bodyText = "Este usuario no podrá entrar en la app.\nEsta acción no se podrá deshacer.",
        confirmText = "Aceptar",
        onConfirm = { onEvent(UserScreenEvent.ConfirmDelete) },
        dismissText = "Cancelar",
        onDismissButton = { onEvent(UserScreenEvent.HideAreYouSureDialog) },
        type = UiType.ERROR
    )
}
