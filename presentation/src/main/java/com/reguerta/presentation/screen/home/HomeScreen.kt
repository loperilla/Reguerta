package com.reguerta.presentation.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Toc
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.navigationDrawer.NavigationDrawerInfo
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.Text
import kotlinx.coroutines.launch

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:45
 * All rights reserved 2024
 */

@Composable
fun homeScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo(Routes.AUTH.route)
        return
    }
    Screen {
        HomeScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navigateTo = navigateTo
        )
    }
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(state, onEvent, navigateTo)
        }
    ) {
        Scaffold(
            topBar = {
                ReguertaTopBar(
                    topBarText = "",
                    navActionClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    navIcon = Icons.Outlined.Menu
                )
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = state.showAreYouSure
            ) {
                LogoutDialog(onEvent)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {

            }
        }
    }
}

@Composable
private fun LogoutDialog(onEvent: (HomeEvent) -> Unit) {
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
        onDismissRequest = { onEvent(HomeEvent.HideDialog) },
        text = {
            TextBody(
                text = "¿Seguro que quieres cerrar la sesión?",
                textSize = TEXT_SIZE_SMALL,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Cerrar sesión",
                textSize = TEXT_SIZE_LARGE,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Confirmar",
                onClick = {
                    onEvent(HomeEvent.GoOut)
                }
            )
        },
        dismissButton = {
            InverseReguertaButton(
                textButton = "Volver",
                onClick = {
                    onEvent(HomeEvent.HideDialog)
                }
            )
        }
    )
}

@Composable
fun DrawerContent(state: HomeState, onEvent: (HomeEvent) -> Unit, navigateTo: (String) -> Unit) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(PADDING_MEDIUM))
        Image(
            painter = painterResource(id = R.mipmap.firstscreenn),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(PADDING_SMALL))
        prepareNavigationDrawerList(
            state.isCurrentUserAdmin,
            state.isCurrentUserProducer,
            onEvent,
            navigateTo
        ).forEach { info ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = info.icon,
                        contentDescription = info.title,
                        tint = Color.White,
                        modifier = Modifier
                            .background(PrimaryColor, CircleShape)
                            .padding(PADDING_EXTRA_SMALL)
                    )
                },
                label = {
                    TextBody(
                        text = info.title,
                        textSize = TEXT_SIZE_LARGE,
                    )
                },
                selected = false,
                onClick = {
                    info.onClick()
                },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

private fun prepareNavigationDrawerList(
    isCurrentUserAdmin: Boolean,
    isCurrentUserProducer: Boolean,
    onEvent: (HomeEvent) -> Unit,
    navigateTo: (String) -> Unit
): List<NavigationDrawerInfo> {
    // Producer = home, pedidos, pedidos recibidos, productos, ajustes, cerrarsesion
    // Admin = home, pedidos, usuarios, noticias, ajustes, cerrarsesion
    val fullList = listOf(
        NavigationDrawerInfo(
            title = "Home",
            icon = Icons.Outlined.Home,
            onClick = {
                navigateTo(Routes.HOME.ROOT.route)
            },
            showInBothCase = true
        ),
        NavigationDrawerInfo(
            title = "Pedidos",
            icon = Icons.AutoMirrored.Filled.Toc,
            onClick = {
                navigateTo(Routes.HOME.ORDERS.route)
            },
            showInBothCase = true
        ),
        NavigationDrawerInfo(
            title = "Pedidos recibidos",
            icon = Icons.Outlined.Inventory,
            onClick = {
                navigateTo(Routes.HOME.ORDER_RECEIVED.route)
            },
            showIfUserIsProducer = true
        ),
        NavigationDrawerInfo(
            title = "Productos",
            icon = Icons.Outlined.Inventory2,
            onClick = {
                navigateTo(Routes.PRODUCTS.route)
            },
            showIfUserIsProducer = true
        ),
        NavigationDrawerInfo(
            title = "Usuarios",
            icon = Icons.Outlined.AccountCircle,
            onClick = {
                navigateTo(Routes.USERS.route)
            },
            showIfUserIsAdmin = true
        ),
        NavigationDrawerInfo(
            title = "Noticias",
            icon = Icons.Outlined.Newspaper,
            onClick = {

            },
            showIfUserIsAdmin = true
        ),
        NavigationDrawerInfo(
            title = "Ajustes",
            icon = Icons.Outlined.Settings,
            onClick = {
                navigateTo(Routes.HOME.SETTINGS.route)
            },
            showInBothCase = true
        ),
        NavigationDrawerInfo(
            title = "Cerrar sesión",
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = {
                onEvent(HomeEvent.ShowDialog)
            },
            showInBothCase = true
        )
    )
    return fullList.filter {
        it.showInBothCase || it.showIfUserIsAdmin && isCurrentUserAdmin || it.showIfUserIsProducer && isCurrentUserProducer
    }
}

@Preview
@Composable
fun HomePreview() {
    Screen {
        HomeScreen(
            state = HomeState(
                isCurrentUserAdmin = false,
                isCurrentUserProducer = false
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}