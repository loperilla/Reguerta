package com.reguerta.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.screen.auth.authGraph
import com.reguerta.presentation.screen.home.homeScreen
import com.reguerta.presentation.screen.home.HomeViewModel
import com.reguerta.presentation.screen.new_order.newOrderGraph
import com.reguerta.presentation.screen.orders.ordersScreen
import com.reguerta.presentation.screen.products.productsGraph
import com.reguerta.presentation.screen.received_orders.receivedOrdersScreen
import com.reguerta.presentation.screen.settings.settingsScreen
import com.reguerta.presentation.screen.users.usersGraph
import com.reguerta.presentation.sync.ForegroundSyncManager
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 30/1/24 at 11:47
 * All rights reserved 2024
 */

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    val isAuthStartDestination = startDestination == Routes.AUTH.route ||
        startDestination == Routes.AUTH.FIRST_SCREEN.route ||
        startDestination == Routes.AUTH.RECOVERY_PASSWORD.route ||
        startDestination == Routes.AUTH.LOGIN.route ||
        startDestination == Routes.AUTH.REGISTER.route

    // Collector global: la señal de foreground se emite desde ReguertaApp (ProcessLifecycleOwner).
    // Aquí la recogemos a nivel root para que funcione aunque Android restaure el backstack en otra pantalla.
    if (!isAuthStartDestination) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        LaunchedEffect(homeViewModel) {
            Timber.tag("SYNC_ForegroundSync").d(
                "MainNavigation: collector activo (startDestination=%s)",
                startDestination
            )
            ForegroundSyncManager.syncRequested.collect {
                Timber.tag("SYNC_ForegroundSync").d("MainNavigation: señal recibida → HomeVM.onAppForegrounded()")
                homeViewModel.onAppForegrounded()
            }
        }
    }

    Screen {
        NavHost(
            navController,
            startDestination = startDestination
        ) {
            authGraph(navController)
            navigation(startDestination = Routes.HOME.ROOT.route, route = Routes.HOME.route) {
                composable(Routes.HOME.ROOT.route) {
                    Timber.i("SYNC_Navegando a HomeScreen (HOME.ROOT)")
                    homeScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.ORDERS.route) {
                    Timber.i("SYNC_Navegando a OrdersScreen (HOME.ORDERS)")
                    ordersScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.ORDER_RECEIVED.route) {
                    Timber.i("SYNC_Navegando a ReceivedOrdersScreen (HOME.ORDER_RECEIVED)")
                    receivedOrdersScreen {
                        navController.navigate(it)
                    }
                }
                composable(Routes.HOME.SETTINGS.route) {
                    Timber.i("SYNC_Navegando a SettingsScreen (HOME.SETTINGS)")
                    settingsScreen {
                        navController.navigate(it)
                    }
                }
            }
            newOrderGraph(navController)
            productsGraph(navController)
            usersGraph(navController)
        }
    }
}