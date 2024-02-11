package com.reguerta.presentation.screen.home

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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.navigationDrawer.NavigationDrawerInfo
import com.reguerta.presentation.ui.DialogBackground
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
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

@OptIn(ExperimentalMaterial3Api::class)
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
            DrawerContent(onEvent, navigateTo)
        }
    ) {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = {

                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (state.showAreYouSure) {
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
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "ExitApp",
                tint = PrimaryColor,
                modifier = Modifier
                    .size(48.dp)
            )
        },
        onDismissRequest = { onEvent(HomeEvent.HideDialog) },
        text = {
            TextBody(
                text = "¿Seguro que quieres cerrar la sesión?",
                textSize = 18.sp,
                textColor = Text
            )
        },
        title = {
            TextTitle(
                text = "Cerrar sesión",
                textSize = 18.sp,
                textColor = Text
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Cerrar sesión",
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
        },
        containerColor = DialogBackground,
        iconContentColor = MaterialTheme.colorScheme.inversePrimary,
    )
}

@Composable
fun DrawerContent(onEvent: (HomeEvent) -> Unit, navigateTo: (String) -> Unit) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.mipmap.firstscreenn),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        prepareNavigationDrawerList(onEvent, navigateTo).forEach { info ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = info.icon,
                        contentDescription = info.title,
                        tint = Color.White,
                        modifier = Modifier
                            .background(PrimaryColor, CircleShape)
                            .padding(4.dp)
                    )
                },
                label = {
                    TextBody(
                        text = info.title,
                        textSize = 18.sp,
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
    onEvent: (HomeEvent) -> Unit,
    navigateTo: (String) -> Unit
): List<NavigationDrawerInfo> {
    return listOf(
        NavigationDrawerInfo(
            title = "Home",
            icon = Icons.Outlined.Home,
            onClick = {
                navigateTo(Routes.HOME.ROOT.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Pedidos",
            icon = Icons.AutoMirrored.Filled.Toc,
            onClick = {
                navigateTo(Routes.HOME.ORDERS.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Pedidos recibidos",
            icon = Icons.Outlined.Inventory,
            onClick = {
                navigateTo(Routes.HOME.ORDER_RECEIVED.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Productos",
            icon = Icons.Outlined.Inventory2,
            onClick = {
                navigateTo(Routes.HOME.PRODUCTS.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Usuarios",
            icon = Icons.Outlined.AccountCircle,
            onClick = {
                navigateTo(Routes.HOME.USERS.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Ajustes",
            icon = Icons.Outlined.Settings,
            onClick = {
                navigateTo(Routes.HOME.SETTINGS.route)
            }
        ),
        NavigationDrawerInfo(
            title = "Cerrar sesión",
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = {
                onEvent(HomeEvent.ShowDialog)
            }
        )
    )
}

@Preview
@Composable
fun HomePreview() {
    Screen {
        LogoutDialog {

        }
    }
}