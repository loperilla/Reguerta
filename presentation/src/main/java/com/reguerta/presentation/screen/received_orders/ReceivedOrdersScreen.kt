package com.reguerta.presentation.screen.received_orders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.mapper.priceFormatted
import com.reguerta.domain.model.received.OrderLineReceived
import com.reguerta.domain.model.received.getAmount
import com.reguerta.domain.model.received.getQuantityByProduct
import com.reguerta.presentation.ALCAZAR
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.image.ProductImage
import com.reguerta.presentation.composables.products.ProductNameUnityContainer
import com.reguerta.presentation.composables.products.ProductNameUnityContainerInMyOrder
import com.reguerta.presentation.ui.Orange
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_72
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.TEXT_TOP_BAR
import com.reguerta.presentation.ui.Text
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
                        orderLines = state.ordersByProduct
                    )
                } else {
                    OrderListByUser(
                        orderLines = state.ordersByUser
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderListByUser(
    orderLines: Map<String, List<OrderLineReceived>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_MEDIUM)
    ) {
        orderLines.forEach {
            item {
                OrderByUser(
                    fullname = it.key,
                    orderLines = it.value
                )
            }
        }
    }
}

@Composable
private fun OrderByUser(
    fullname: String,
    orderLines: List<OrderLineReceived>
) {
    ReguertaCard(
        modifier = Modifier.padding(PADDING_SMALL),
        content = {
            TextTitle(
                text = fullname,
                textColor = PrimaryColor,
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .fillMaxWidth(),
                textAlignment = TextAlign.Start
            )
            HorizontalDivider()
            orderLines.forEach {
                ProductOrders(
                    quantity = it.quantity,
                    product = it.product
                )
                HorizontalDivider()
            }
            TextTitle(
                text = "Total: ${orderLines.getAmount()}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_SMALL),
                textColor = Orange,
                textSize = TEXT_SIZE_LARGE,
                textAlignment = TextAlign.End
            )
        }
    )
}

@Composable
private fun ProductOrders(
    quantity: Int,
    product: Product
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(PADDING_EXTRA_SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.6f)
        ) {
            ProductNameUnityContainerInMyOrder(product)
        }
        VerticalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.2f)
        ) {
            TextBody(
                text = "$quantity",
                textSize = TEXT_SIZE_MEDIUM,
                textColor = Text,
                modifier = Modifier.padding(PADDING_EXTRA_SMALL),
            )
            TextBody(
                text = product.container,
                textSize = TEXT_SIZE_SMALL,
                textColor = Text,
                modifier = Modifier.padding(PADDING_EXTRA_SMALL),
            )
        }
        VerticalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.2f)
        ) {
            TextBody(
                text = product.priceFormatted(),
                textSize = TEXT_SIZE_MEDIUM,
                textColor = Orange
            )
        }
    }
}

@Composable
fun OrderListByProduct(
    orderLines: Map<Product, List<OrderLineReceived>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_MEDIUM)
    ) {
        orderLines.forEach {
            item {
                OrderByProduct(
                    product = it.key,
                    orderLines = it.value
                )
            }
        }
    }
}

@Composable
fun OrderByProduct(
    product: Product,
    orderLines: List<OrderLineReceived>,
    modifier: Modifier = Modifier
) {
    ReguertaCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        content = {
            Row(
                //horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(PADDING_EXTRA_SMALL)
            ) {
                ProductImage(
                    product = product,
                    imageSize = SIZE_72,
                    modifier = Modifier.wrapContentWidth()
                )
                VerticalDivider(
                    thickness = 1.dp,
                    color = Color.Black
                )
                ProductNameUnityContainer(
                    product,
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(0.6f)
                )
                VerticalDivider(
                    thickness = 1.dp,
                    color = Color.Black
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.2f)
                ) {
                    TextTitle(
                        text = "${orderLines.getQuantityByProduct(product)}",
                        textSize = TEXT_TOP_BAR,
                        textColor = Text,
                        modifier = Modifier.padding(PADDING_EXTRA_SMALL),
                    )
                    TextBody(
                        text = product.container,
                        textSize = TEXT_SIZE_SMALL,
                        textColor = Text,
                        modifier = Modifier.padding(PADDING_EXTRA_SMALL),
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun ReceivedOrdersPreview() {
    Screen {
        ReceivedOrdersScreen(
            state = ReceivedOrdersState(
                ordersByProduct = mapOf(
                    ALCAZAR to listOf(
                        OrderLineReceived(
                            orderName = "Manuel",
                            orderSurname = "Lopera",
                            product = ALCAZAR,
                            quantity = 1,
                            companyName = "",
                        )
                    )
                ),
                ordersByUser = mapOf(
                    "Manuel Lopera" to listOf(
                        OrderLineReceived(
                            orderName = "Manuel",
                            orderSurname = "Lopera",
                            product = ALCAZAR,
                            quantity = 1,
                            companyName = "",
                        )
                    )
                )
            ),
            onEvent = {}
        )
    }
}