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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaHomeTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.navigation.NavigationDrawerInfo
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.navigation.Routes
import kotlinx.coroutines.launch
import com.reguerta.domain.repository.ConfigCheckResult
import com.reguerta.domain.enums.UiType
import androidx.core.net.toUri
import com.reguerta.domain.enums.afterDays
import com.reguerta.domain.enums.isReservedDayFor
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.presentation.composables.ReguertaOrderButton
import com.reguerta.presentation.sync.ForegroundSyncManager
import com.reguerta.presentation.toWeekDay
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

    var showInitialRetry by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // Watchdog: si el loader inicial dura demasiado, mostramos "Reintentar"
    LaunchedEffect(showInitialLoader) {
        if (showInitialLoader) {
            kotlinx.coroutines.delay(20_000)
            if (showInitialLoader) {
                showInitialRetry = true
                Timber.w("SYNC_Watchdog: loader inicial excede 20s → mostrar botón Reintentar")
            }
        } else {
            showInitialRetry = false
        }
    }

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
                Spacer(modifier = Modifier.height(Dimens.Spacing.md))
                androidx.compose.material3.Text("Preparando datos por primera vez...", color = MaterialTheme.colorScheme.primary)
                if (showInitialRetry) {
                    Spacer(modifier = Modifier.height(Dimens.Spacing.sm))
                    ReguertaButton(
                        textButton = "Reintentar",
                        isSingleButton = true,
                        onClick = {
                            Timber.i("SYNC_UI: Reintentar (loader inicial)")
                            showInitialRetry = false
                            // Reintentamos: recargar config y disparar sync si la tenemos
                            configViewModel.loadConfig()
                            val loaded = config
                            if (loaded != null) {
                                viewModel.triggerSyncIfNeeded(
                                    config = loaded,
                                    isAdmin = state.isCurrentUserAdmin,
                                    isProducer = state.isCurrentUserProducer,
                                    currentDay = state.currentDay,
                                    deliveryDay = state.deliveryDay
                                )
                            }
                        }
                    )
                }
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
    val configViewModel: ConfigViewModel = hiltViewModel()
    // Loader bloqueante (solo si NO está el loader inicial)
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingAnimation()
                Spacer(modifier = Modifier.height(Dimens.Spacing.sm))
                androidx.compose.material3.Text(
                    "Cargando datos…",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(Dimens.Spacing.sm))
                ReguertaButton(
                    textButton = "Reintentar",
                    isSingleButton = true,
                    onClick = {
                        Timber.i("SYNC_UI: Reintentar (loader secundario)")
                        // Reintentamos: recargar configuración y forzar sync si es posible
                        configViewModel.loadConfig()
                        // Intento “suave”: si hay config cargada, disparamos sync de nuevo
                        // (si aún no la hay, el init del VM debería completar y apagar el loader)
                        val cfg = configViewModel.config.value
                        if (cfg != null) {
                            viewModel.triggerSyncIfNeeded(
                                config = cfg,
                                isAdmin = state.isCurrentUserAdmin,
                                isProducer = state.isCurrentUserProducer,
                                currentDay = state.currentDay,
                                deliveryDay = state.deliveryDay
                            )
                        }
                    }
                )
            }
        }
        return
    }

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(state, onEvent, navigateTo)
        }
    ) {
        ReguertaScaffold(
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
                        if (state.currentDay.toWeekDay().isReservedDayFor(state.deliveryDay)) {
                            onEvent(HomeEvent.ShowBlockedDayDialog)
                        } else {
                            Timber.i("SYNC_Botón Mi pedido pulsado, navegando a: ${Routes.ORDERS.NEW.route}")
                            navigateTo(Routes.ORDERS.NEW.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.md, vertical = Dimens.Spacing.sm)
                )

                if (state.isCurrentUserProducer) {
                    ShowYourOrdersButton(
                        buttonIsEnabled = !state.deliveryDay.afterDays().contains(state.currentDay.toWeekDay()),
                        onButtonClick = {
                            Timber.i("SYNC_Botón Ver tus pedidos pulsado, navegando a: ${Routes.HOME.ORDER_RECEIVED.route}")
                            navigateTo(Routes.HOME.ORDER_RECEIVED.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Spacing.md, vertical = Dimens.Spacing.sm)
                    )
                }


            }
            // Blocked day dialog
            if (state.showBlockedDayDialog) {
                ReguertaAlertDialog(
                    onDismissRequest = { onEvent(HomeEvent.HideBlockedDayDialog) },
                    icon = Icons.Default.Info,
                    titleText = "¡Atención!",
                    bodyText = "El día posterior al reparto se ha reservado para que los productores hagan cambios. Los pedidos a partir de mañana y hasta el domingo.",
                    confirmText = "Aceptar",
                    onConfirm = { onEvent(HomeEvent.HideBlockedDayDialog) },
                    containerColor = MaterialTheme.colorScheme.background,
                    type = UiType.INFO
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
    ReguertaOrderButton(
        text = "Mi pedido",
        onClick = onButtonClick,
        modifier = modifier,
        enabled = buttonIsEnabled
    )
}

@Composable
private fun ShowYourOrdersButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonIsEnabled: Boolean = true
) {
    ReguertaOrderButton(
        text = "Ver tus pedidos",
        onClick = onButtonClick,
        modifier = modifier,
        enabled = buttonIsEnabled
    )
}

@Composable
private fun LogoutDialog(onEvent: (HomeEvent) -> Unit) {
    ReguertaAlertDialog(
        onDismissRequest = { onEvent(HomeEvent.HideDialog) },
        icon = Icons.Default.Info,
        titleText = "Cerrar sesión",
        bodyText = "¿Estás seguro que quieres cerrar la sesión?",
        confirmText = "Confirmar",
        onConfirm = { onEvent(HomeEvent.GoOut) },
        dismissText = "Volver",
        onDismissButton = { onEvent(HomeEvent.HideDialog) },
        containerColor = MaterialTheme.colorScheme.background,
        type = UiType.INFO
    )
}

@Composable
fun DrawerContent(state: HomeState, onEvent: (HomeEvent) -> Unit, navigateTo: (String) -> Unit) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(Dimens.Spacing.md))
        Image(
            painter = painterResource(id = R.mipmap.firstscreenn),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(Dimens.Spacing.sm))
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
                            .padding(Dimens.Spacing.xs)
                    )
                },
                label = {
                    TextBody(
                        text = info.title,
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
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
            text = "android version 0.2.1.7",
            textSize = MaterialTheme.typography.bodyMedium.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(Dimens.Spacing.sm)
        )
    }
}

@Composable
private fun showNotAuthorizedDialog() {
    ReguertaAlertDialog(
        onDismissRequest = { },
        icon = Icons.Default.Warning,
        titleText = "Usuario no autorizado",
        bodyText = "Ponte en contacto con algún miembro de La Regüerta para que te den acceso.",
        containerColor = MaterialTheme.colorScheme.background,
        type = UiType.ERROR
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
        onDismissRequest = { /* No permitir cerrar explícitamente */ },
        icon = Icons.Default.Warning,
        titleText = "Actualización obligatoria",
        bodyText = "Para seguir usando la app necesitas actualizarla a la versión mínima requerida.",
        confirmText = "Actualizar",
        onConfirm = {
            val storeUrl = config?.versions?.get("android")?.storeUrl.orEmpty()
            if (storeUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, storeUrl.toUri())
                context.startActivity(intent)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        type = UiType.ERROR
    )
}

@Composable
private fun RecommendUpdateDialog(
    config: ConfigModel?,
    context: Context,
    onEvent: (HomeEvent) -> Unit
) {
    ReguertaAlertDialog(
        onDismissRequest = { /* Cerrable si lo deseas */ },
        icon = Icons.Default.Info,
        titleText = "Actualización disponible",
        bodyText = "Hay una nueva versión de la app disponible. Te recomendamos actualizar para disfrutar de las últimas mejoras.",
        confirmText = "Actualizar",
        onConfirm = {
            val storeUrl = config?.versions?.get("android")?.storeUrl.orEmpty()
            if (storeUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, storeUrl.toUri())
                context.startActivity(intent)
            }
        },
        dismissText = "Cerrar",
        onDismissButton = { onEvent(HomeEvent.HideDialog) },
        containerColor = MaterialTheme.colorScheme.background,
        type = UiType.INFO
    )
}
