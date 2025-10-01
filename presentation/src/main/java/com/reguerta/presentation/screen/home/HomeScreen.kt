package com.reguerta.presentation.screen.home

import com.reguerta.presentation.composables.LoadingAnimation

import com.reguerta.presentation.screen.config.ConfigViewModel
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaHomeTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.navigationDrawer.NavigationDrawerInfo
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
import com.reguerta.presentation.ui.TEXT_SIZE_SPECIAL_BTN
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import com.reguerta.domain.repository.ConfigCheckResult
import androidx.core.net.toUri
import com.reguerta.domain.enums.nextDay
import com.reguerta.domain.enums.toJavaDayOfWeek
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.presentation.sync.ForegroundSyncManager
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

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
    val configViewModel: ConfigViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        configViewModel.loadConfig()
    }
    val config by configViewModel.config.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- Loader primer arranque y recarga productos tras sync global ---
    // State para el loader inicial
    var showInitialLoader by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    val isFirstRun by viewModel.isFirstRun.collectAsStateWithLifecycle()
    val isSyncFinished by viewModel.isSyncFinished.collectAsStateWithLifecycle()

    LaunchedEffect(isFirstRun, isSyncFinished) {
        if (isFirstRun && isSyncFinished) {
            viewModel.preloadCriticalDataIfNeeded()
            kotlinx.coroutines.delay(2000)
            showInitialLoader = false
            Timber.i("SYNC_Loader inicial terminado, mostrando Home con botones activos")
            // viewModel.forceSync() // Eliminado: No recargamos explícitamente tras el loader inicial
            viewModel.setFirstRunFalse()
        }
        if (!isFirstRun) showInitialLoader = false
    }
    // --- Fin loader primer arranque ---

    // Loader bloqueante inicial: solo si showInitialLoader es true
    if (showInitialLoader) {
        Timber.i("SYNC_Mostrando loader inicial (primer arranque o esperando datos críticos)")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingAnimation()
                Spacer(modifier = Modifier.height(PADDING_MEDIUM))
                androidx.compose.material3.Text("Preparando datos por primera vez...", color = MaterialTheme.colorScheme.primary)
            }
        }
        return
    }
    Timber.i("SYNC_Montando Composable homeScreen - loader inicial ya ha desaparecido")

    if (state.goOut) {
        navigateTo(Routes.AUTH.route)
        return
    }
    if (state.showNotAuthorizedDialog) {
        showNotAuthorizedDialog()
    }
    when (state.configCheckResult) {
        ConfigCheckResult.ForceUpdate -> ForceUpdateDialog(config, context)
        ConfigCheckResult.RecommendUpdate -> RecommendUpdateDialog(config, context, viewModel::onEvent)
        else -> {}
    }
    Screen {
        HomeScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navigateTo = navigateTo,
            viewModel = viewModel
        )
    }
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    navigateTo: (String) -> Unit,
    viewModel: HomeViewModel
) {
    Timber.i("SYNC_Montando Composable HomeScreen (privado)")
    // Loader bloqueante (solo si NO está el loader inicial)
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingAnimation()
        }
        return
    }
    val configViewModel: ConfigViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        ForegroundSyncManager.syncRequested.collectLatest {
            val loadedConfig = requireNotNull(configViewModel.config.first { it != null })
            viewModel.triggerSyncIfNeeded(
                config = loadedConfig,
                isAdmin = state.isCurrentUserAdmin,
                isProducer = state.isCurrentUserProducer,
                currentDay = state.currentDay,
                deliveryDay = state.deliveryDay
            )
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Calcular el día bloqueado: el día siguiente a deliveryDay, excepto si es sábado o domingo
    val deliveryDay = state.deliveryDay.toJavaDayOfWeek()
    val blockedDay = if (deliveryDay == DayOfWeek.SATURDAY || deliveryDay == DayOfWeek.SUNDAY) {
        null
    } else {
        state.deliveryDay.nextDay().toJavaDayOfWeek()
    }
    val isBlockedDay = blockedDay != null && state.currentDay == blockedDay

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(state, onEvent, navigateTo)
        }
    ) {
        Scaffold(
            topBar = {
                ReguertaHomeTopBar(
                    navActionClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    navIcon = Icons.Outlined.Menu
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = state.showAreYouSure
            ) {
                LogoutDialog(onEvent)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                MakeYourOrderButton(
                    onButtonClick = {
                        if (isBlockedDay) {
                            onEvent(HomeEvent.ShowBlockedDayDialog)
                        } else {
                            Timber.i("SYNC_Botón Mi pedido pulsado, navegando a: ${Routes.ORDERS.NEW.route}")
                            navigateTo(Routes.ORDERS.NEW.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                )

                if (state.isCurrentUserProducer && state.currentDay in DayOfWeek.MONDAY..state.deliveryDay.toJavaDayOfWeek()) {
                    ShowYourOrderButton(
                        onButtonClick = {
                            Timber.i("SYNC_Botón Ver tus pedidos pulsado, navegando a: ${Routes.HOME.ORDER_RECEIVED.route}")
                            navigateTo(Routes.HOME.ORDER_RECEIVED.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_SMALL)
                    )
                }
            }
            // Blocked day dialog
            if (state.showBlockedDayDialog) {
                ReguertaAlertDialog(
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(SIZE_88)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2F), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(SIZE_48)
                            )
                        }
                    },
                    onDismissRequest = { onEvent(HomeEvent.HideBlockedDayDialog) },
                    text = {
                        TextBody(
                            text = "El día posterior al reparto se ha reservado para que los productores hagan cambios. Los pedidos a partir de mañana y hasta el domingo.",
                            textSize = TEXT_SIZE_DLG_BODY,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            textAlignment = TextAlign.Center
                        )
                    },
                    title = {
                        TextTitle(
                            text = "¡Atención!",
                            textSize = TEXT_SIZE_DLG_TITLE,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            textAlignment = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            ReguertaButton(
                                textButton = "Aceptar",
                                isSingleButton = true,
                                onClick = { onEvent(HomeEvent.HideBlockedDayDialog) }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MakeYourOrderButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonIsEnabled: Boolean = true
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier,
        shape = RoundedCornerShape(32f),
        enabled = buttonIsEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(0.2f)
        )
    ) {
        TextBody(
            text = "Mi pedido",
            textSize = TEXT_SIZE_SPECIAL_BTN,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(PADDING_SMALL)
        )
    }
}

@Composable
private fun ShowYourOrderButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonIsEnabled: Boolean = true
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier,
        shape = RoundedCornerShape(32f),
        enabled = buttonIsEnabled,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.2f))
    ) {
        TextBody(
            text = "Ver tus pedidos",
            textSize = TEXT_SIZE_SPECIAL_BTN,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(PADDING_SMALL)
        )
    }
}

@Composable
private fun LogoutDialog(onEvent: (HomeEvent) -> Unit) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(HomeEvent.HideDialog) },
        text = {
            TextBody(
                text = "¿Estás seguro que quieres cerrar la sesión?",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Cerrar sesión",
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InverseReguertaButton(
                    textButton = "Volver",
                    isSingleButton = false,
                    onClick = { onEvent(HomeEvent.HideDialog) },
                    modifier = Modifier.weight(0.48f)
                )
                Spacer(modifier = Modifier.width(PADDING_EXTRA_SMALL))
                ReguertaButton(
                    textButton = "Confirmar",
                    isSingleButton = false,
                    onClick = { onEvent(HomeEvent.GoOut) },
                    modifier = Modifier.weight(0.52f)
                )
            }
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
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
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextBody(
            text = "android version 0.2.1.1",
            textSize = TEXT_SIZE_MEDIUM,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(PADDING_SMALL)
        )
    }
}

@Composable
private fun showNotAuthorizedDialog() {
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
        onDismissRequest = { },
        text = {
            TextBody(
                text = "Ponte en contacto con algún miembro de La Regüerta para que te den acceso.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Usuario no autorizado",
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        }
    )
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
//        NavigationDrawerInfo(
//            title = "Pedidos",
//            icon = Icons.AutoMirrored.Filled.Toc,
//            onClick = {
//                navigateTo(Routes.HOME.ORDERS.route)
//            },
//            showInBothCase = true
//        ),
//        NavigationDrawerInfo(
//            title = "Pedidos recibidos",
//            icon = Icons.Outlined.Inventory,
//            onClick = {
//                navigateTo(Routes.HOME.ORDER_RECEIVED.route)
//            },
//            showIfUserIsProducer = true
//        ),
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
//        NavigationDrawerInfo(
//            title = "Noticias",
//            icon = Icons.Outlined.Newspaper,
//            onClick = {
//
//            },
//            showIfUserIsAdmin = true
//        ),
//        NavigationDrawerInfo(
//            title = "Ajustes",
//            icon = Icons.Outlined.Settings,
//            onClick = {
//                navigateTo(Routes.HOME.SETTINGS.route)
//            },
//            showInBothCase = true
//        ),
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

@Composable
private fun ForceUpdateDialog(config: ConfigModel?, context: Context) {
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
                    contentDescription = "Actualización requerida",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { /* No permitir cerrar */ },
        text = {
            TextBody(
                text = "Para seguir usando la app necesitas actualizarla a la versión mínima requerida.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Actualización obligatoria",
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                ReguertaButton(
                    textButton = "Actualizar",
                    isSingleButton = true,
                    onClick = {
                        val storeUrl = config?.versions?.get("android")?.storeUrl.orEmpty()
                        if (storeUrl.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, storeUrl.toUri())
                            context.startActivity(intent)
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}

@Composable
private fun RecommendUpdateDialog(config: ConfigModel?,
                                  context: Context,
                                  onEvent: (HomeEvent) -> Unit) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { /* Cerrable si lo deseas */ },
        text = {
            TextBody(
                text = "Hay una nueva versión de la app disponible. Te recomendamos actualizar para disfrutar de las últimas mejoras.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Actualización disponible",
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InverseReguertaButton(
                    textButton = "Cerrar",
                    isSingleButton = false,
                    onClick = { onEvent(HomeEvent.HideDialog) },
                    modifier = Modifier.weight(0.48f)
                )
                Spacer(modifier = Modifier.width(PADDING_EXTRA_SMALL))
                ReguertaButton(
                    textButton = "Actualizar",
                    isSingleButton = false,
                    onClick = {
                        val storeUrl = config?.versions?.get("android")?.storeUrl.orEmpty()
                        if (storeUrl.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, storeUrl.toUri())
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.weight(0.52f)
                )
            }
        }
    )
}
