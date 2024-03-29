package com.reguerta.presentation.screen.received_orders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import kotlinx.coroutines.launch

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.received_orders
 * Created By Manuel Lopera on 6/2/24 at 16:37
 * All rights reserved 2024
 */

@Composable
fun receivedOrdersScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<ReceivedOrdersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo(Routes.HOME.route)
        return
    }
    Screen {
        ReceivedOrdersScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReceivedOrdersScreen(
    state: ReceivedOrdersState,
    onEvent: (ReceivedOrdersEvent) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { tabList.size }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Pedidos a preparar",
                navActionClick = {
                    onEvent(ReceivedOrdersEvent.GoOut)
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                tabList.fastForEachIndexed { i, receivedTab ->
                    Tab(
                        selected = pagerState.currentPage == i,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(i)
                            }
                        },
                        text = {
                            TextTitle(
                                text = receivedTab.title,
                                textSize = TEXT_SIZE_LARGE
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState
            ) { index ->
                if (index == 0) {
                    OrderListByProduct(
                        state = state,
                        onEvent = onEvent
                    )
                } else {
                    OrderListByUser(
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderListByUser(
    state: ReceivedOrdersState,
    onEvent: (ReceivedOrdersEvent) -> Unit
) {
    TextTitle(
        text = "Pedidos por usuario"
    )
}

@Composable
private fun OrderListByProduct(
    state: ReceivedOrdersState,
    onEvent: (ReceivedOrdersEvent) -> Unit
) {
    TextTitle(
        text = "Pedidos por producto"
    )
}

@Preview
@Composable
fun ReceivedOrdersPreview() {
    Screen {
        ReceivedOrdersScreen(
            state = ReceivedOrdersState(),
            onEvent = {}
        )
    }
}