package com.reguerta.presentation.screen.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.orders
 * Created By Manuel Lopera on 6/2/24 at 16:28
 * All rights reserved 2024
 */

@Composable
fun ordersScreen(navigateTo: (String) -> Unit) {
    val viewModel = hiltViewModel<OrdersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo(Routes.HOME.ROOT.route)
        return
    }
    Screen {
        OrdersScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrderState,
    onEvent: (OrderEvent) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    TextTitle(
                        text = "Pedidos",
                        textSize = 26.sp,
                        textColor = PrimaryColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(OrderEvent.GoOut)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {

        }
    }
}