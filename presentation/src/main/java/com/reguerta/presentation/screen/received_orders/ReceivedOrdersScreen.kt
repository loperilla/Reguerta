package com.reguerta.presentation.screen.received_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.OrderLineReceived
import com.reguerta.domain.model.getAmount
import com.reguerta.domain.model.getQuantityByProduct
import com.reguerta.presentation.composables.LoadingAnimation
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.ProductImage
import com.reguerta.presentation.composables.ProductNameUnityContainer
import com.reguerta.presentation.composables.ProductNameUnityContainerInMyOrder
import com.reguerta.presentation.getQuantitySum
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.navigation.Routes
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

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation()
            }
        } else {
            Screen {
                ReceivedOrdersScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
fun ReceivedOrdersScreen(
    state: ReceivedOrdersState,
    onEvent: (ReceivedOrdersEvent) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { tabList.size }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    ReguertaScaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Pedidos a preparar",
                navActionClick = {
                    onEvent(ReceivedOrdersEvent.GoOut)
                }
            )
        }
    ) { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isLoading -> {
                    LoadingAnimation()
                }
                state.ordersByProduct.isEmpty() -> {
                    ReguertaCard(
                        modifier = Modifier.padding(horizontal = Dimens.Spacing.md, vertical = Dimens.Spacing.md),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.Spacing.lg)
                            ) {
                                TextTitle(
                                    text = "No has recibido ningún pedido esta semana.",
                                    textColor = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlignment = TextAlign.Center
                                )
                            }
                        }
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
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
                                            textSize = MaterialTheme.typography.titleMedium.fontSize
                                        )
                                    }
                                )
                            }
                        }
                        HorizontalPager(state = pagerState) { index ->
                            if (index == 0) {
                                OrderListByProduct(
                                    orderLines = state.ordersByProduct,
                                    state = state
                                )
                            } else {
                                OrderListByUser(
                                    orderLines = state.ordersByUser,
                                    state = state
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListByUser(
    orderLines: Map<String, List<OrderLineReceived>>,
    state: ReceivedOrdersState,
    modifier: Modifier = Modifier
) {
    val grandTotal = orderLines.values.flatten().sumOf { it.subtotal }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = Dimens.Spacing.zero)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(Dimens.Spacing.sm)
        ) {
            orderLines.forEach {
                item {
                    OrderByUser(
                        fullname = it.key,
                        orderLines = it.value,
                        state = state
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .navigationBarsPadding()
                .padding(Dimens.Spacing.sm),
            contentAlignment = Alignment.Center
        ) {
            TextTitle(
                text = "Suma total general: %.2f €".format(grandTotal),
                textSize = MaterialTheme.typography.titleLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun OrderByUser(
    fullname: String,
    orderLines: List<OrderLineReceived>,
    state: ReceivedOrdersState
) {
    ReguertaCard(
        modifier = Modifier.padding(Dimens.Spacing.sm),
        content = {
            TextTitle(
                text = fullname,
                textColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(Dimens.Spacing.sm)
                    .fillMaxWidth(),
                textAlignment = TextAlign.Center
            )
            HorizontalDivider()
            orderLines.forEach {
                ProductOrders(
                    orderLine = it,
                    state = state
                )
                HorizontalDivider()
            }
            TextTitle(
                text = "Total: ${orderLines.getAmount()}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Spacing.sm),
                textColor = MaterialTheme.colorScheme.error,
                textSize = MaterialTheme.typography.titleMedium.fontSize,
                textAlignment = TextAlign.End
            )
        }
    )
}

@Composable
private fun ProductOrders(
    orderLine: OrderLineReceived,
    state: ReceivedOrdersState
) {
    val quantitySum = getQuantitySum(orderLine, state.containers, state.measures)
    val totalPrice =  orderLine.subtotal//orderLine.product.price * orderLine.quantity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Dimens.Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.58f)
        ) {
            ProductNameUnityContainerInMyOrder(orderLine.product)
        }
        VerticalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.22f)
        ) {
            TextBody(
                text = "${orderLine.quantity}",
                textSize = MaterialTheme.typography.bodyLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(Dimens.Spacing.xs),
            )
            TextBody(
                text = quantitySum,
                textSize = MaterialTheme.typography.bodySmall.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(Dimens.Spacing.xs),
            )
        }
        VerticalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.2f)
        ) {
            TextBody(
                text = "%.2f €".format(totalPrice),
                textSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun OrderListByProduct(
    orderLines: Map<Product, List<OrderLineReceived>>,
    state: ReceivedOrdersState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.md),
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.Spacing.md)
    ) {
        orderLines.forEach {
            item {
                OrderByProduct(
                    product = it.key,
                    orderLines = it.value,
                    state = state
                )
            }
        }
    }
}

@Composable
fun OrderByProduct(
    product: Product,
    orderLines: List<OrderLineReceived>,
    state: ReceivedOrdersState,
    modifier: Modifier = Modifier
) {
    val quantitySum = remember(state) {
        getQuantitySum(orderLines.first { it.product == product }, state.containers, state.measures)
    }
    ReguertaCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(Dimens.Spacing.xs)
            ) {
                ProductImage(
                    product = product,
                    imageSize = Dimens.Size.dp64,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(0.2f)
                        .padding(Dimens.Spacing.zero)
                )
                VerticalDivider()
                ProductNameUnityContainer(
                    product,
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(0.6f)
                )
                VerticalDivider()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(0.2f)
                ) {
                    TextTitle(
                        text = "${orderLines.getQuantityByProduct(product)}",
                        textSize = MaterialTheme.typography.titleLarge.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(Dimens.Spacing.xs),
                    )
                    TextBody(
                        text = quantitySum,
                        textSize = MaterialTheme.typography.bodySmall.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(Dimens.Spacing.xs),
                    )
                }
            }
        }
    )
}
