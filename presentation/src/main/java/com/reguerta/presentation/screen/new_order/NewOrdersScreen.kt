package com.reguerta.presentation.screen.new_order

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.enums.Pane
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.mapper.containerUnity
import com.reguerta.domain.model.mapper.getAmount
import com.reguerta.domain.model.mapper.getUnitType
import com.reguerta.domain.model.mapper.priceFormatted
import com.reguerta.domain.model.OrderLineReceived
import com.reguerta.domain.model.getDblAmount
import com.reguerta.presentation.composables.AmountText
import com.reguerta.presentation.composables.BtnType
import com.reguerta.presentation.composables.HeaderSectionText
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.LoadingAnimation
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.StockOrderText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.image.ProductImage
import com.reguerta.presentation.composables.products.ProductNameUnityContainerInMyOrder
import com.reguerta.presentation.getQuantitySum
import com.reguerta.presentation.ui.PADDING_EXTRA_LARGE
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_LARGE
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PADDING_ULTRA_SMALL
import com.reguerta.presentation.ui.PADDING_ZERO
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_88
import com.reguerta.presentation.ui.SIZE_96
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_BODY
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_TITLE
import com.reguerta.presentation.ui.TEXT_SIZE_EXTRA_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_EXTRA_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.TEXT_SPECIAL
import com.reguerta.presentation.ui.TEXT_TOP_BAR
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:10
 * All rights reserved 2024
 */

@Composable
fun newOrderScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<NewOrderViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.goOut) {
        navigateTo(Routes.HOME.ROOT.route)
        return
    }

    if (state.showPopup == PopupType.ARE_YOU_SURE_DELETE) {
        AreYouSureDeletePopup(
            onEvent = viewModel::onEvent,
            isDeletingOrder = state.isDeletingOrder
        )
    }

    if (state.showPopup == PopupType.ORDER_ADDED) {
        ConfirmPopup(
            title = buildAnnotatedString {
                append("Pedido realizado con éxito")
            },
            body = buildAnnotatedString {
                append("Recuerda que puedes modificar el pedido antes de que acabe el domingo")
            },
            onEvent = viewModel::onEvent,
            confirmButton = {
                viewModel.onEvent(NewOrderEvent.GoOut)
            }
        )
    }

    if (state.showPopup == PopupType.MISSING_COMMIT) {
        WrongPopup(
            title = buildAnnotatedString {
                append("El pedido no se puede realizar")
            },
            body = buildAnnotatedString {
                append(state.errorMessage ?: "Faltan productos de tu compromiso en tu pedido.")
            },
            onEvent = viewModel::onEvent,
            confirmButton = {
                viewModel.onEvent(NewOrderEvent.HideDialog)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state.uiState) {
            NewOrderUiMode.LOADING -> {
                Timber.i("SYNC_UI: Loading visible! state.uiState = LOADING")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }
            NewOrderUiMode.SELECT_PRODUCTS -> {
                Timber.i("SYNC_UI: Modo selección de productos.")
                Screen {
                    NewOrderScreen(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                }
            }
            NewOrderUiMode.EDIT_ORDER -> {
                Timber.i("SYNC_UI: Modo edición de pedido existente.")
                Screen {
                    ExistingOrderScreen(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                }
            }
            NewOrderUiMode.SHOW_PREVIOUS_ORDER -> {
                Timber.i("SYNC_UI: Modo mostrar pedido anterior.")
                Screen {
                    LastOrderScreen(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                }
            }
            NewOrderUiMode.ERROR -> {
                Timber.i("SYNC_UI: Error visible. Mensaje: ${state.errorMessage}")
                Screen {
                    NoOrderScreen(
                        onEvent = viewModel::onEvent
                    )
                    // Opcional: muestra mensaje de error más explícito usando state.errorMessage
                }
            }
        }
    }
}

@Composable
fun NoOrderScreen(
    onEvent: (NewOrderEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mi último pedido",
                navActionClick = {
                    onEvent(NewOrderEvent.GoOut)
                },
                actions = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PADDING_ZERO),
                        horizontalAlignment = Alignment.End
                    ) {

                    }
                }
            )
        }
    ) {
        ReguertaCard(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_MEDIUM),
            containerColor = MaterialTheme.colorScheme.error.copy(0.15f),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PADDING_LARGE),
                    contentAlignment = Alignment.Center
                ) {
                    TextTitle(
                        text = "No hay ningún pedido registrado.",
                        textColor = MaterialTheme.colorScheme.error,
                        textAlignment = TextAlign.Center
                    )
                }
            }
        )
    }
}

@Composable
fun ExistingOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    val grandTotal = state.orderLinesByCompanyName.values.sumOf { it.getDblAmount() }

    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mi pedido",
                navActionClick = { onEvent(NewOrderEvent.GoOut) },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBody(
                            text = "¿eliminar?",
                            textColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = PADDING_SMALL)
                        )
                        ReguertaIconButton(
                            onClick = { onEvent(NewOrderEvent.ShowAreYouSureDeleteOrder) },
                            iconButton = Icons.Filled.Delete,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background( MaterialTheme.colorScheme.primary)
                    .padding(PADDING_SMALL),
                contentAlignment = Alignment.Center
            ) {
                TextTitle(
                    text = "Suma total pedido: %.2f €".format(grandTotal),
                    textSize = TEXT_TOP_BAR,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.orderLinesByCompanyName.values.flatten().isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            } else {
                OrderLinesByCompany(
                    orderLines = state.orderLinesByCompanyName,
                    state =  state,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun LastOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    val grandTotal = state.orderLinesByCompanyName.values.sumOf { it.getDblAmount() }

    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mi último pedido",
                navActionClick = { onEvent(NewOrderEvent.GoOut) }
                // sin 'actions'
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background( MaterialTheme.colorScheme.primary)
                    .padding(PADDING_SMALL),
                contentAlignment = Alignment.Center
            ) {
                TextTitle(
                    text = "Suma total pedido: %.2f €".format(grandTotal),
                    textSize = TEXT_TOP_BAR,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.orderLinesByCompanyName.values.flatten().isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            } else {
                OrderLinesByCompany(
                    orderLines = state.orderLinesByCompanyName,
                    state = state,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OrderLinesByCompany(
    orderLines: Map<String, List<OrderLineReceived>>,
    state: NewOrderState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_SMALL)
    ) {
        orderLines.forEach {
            item {
                OrderByCompany(
                    companyName = it.key,
                    orderLines = it.value,
                    state = state
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun OrderByCompany(
    companyName: String,
    orderLines: List<OrderLineReceived>,
    state: NewOrderState
) {
    ReguertaCard(
        modifier = Modifier.padding(
            horizontal = PADDING_SMALL,
            vertical = PADDING_SMALL
        ),
        content = {
            TextBody(
                text = companyName,
                textSize = TEXT_SIZE_LARGE,
                textColor =  MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(
                        top = PADDING_SMALL,
                        bottom = PADDING_EXTRA_SMALL,
                        start = PADDING_MEDIUM
                    ),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = PADDING_ZERO))

            orderLines.forEach {
                ProductLine(
                    quantity = it.quantity,
                    subtotal = it.subtotal,
                    product = it.product,
                    orderLines = orderLines,
                    state = state
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = PADDING_EXTRA_SMALL))

            TextBody(
                text = "Total: ${String.format("%.2f", orderLines.getDblAmount())}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = PADDING_SMALL,
                        top = PADDING_EXTRA_SMALL,
                        end = PADDING_MEDIUM
                    ),
                textColor = MaterialTheme.colorScheme.error,
                textSize = TEXT_SIZE_EXTRA_LARGE,
                textAlignment = TextAlign.End
            )
        }
    )
}

@Composable
private fun ProductLine(
    quantity: Int,
    subtotal: Double,
    product: Product,
    orderLines: List<OrderLineReceived>,
    state: NewOrderState
) {
    val quantitySum = remember(state) {
        getQuantitySum(orderLines.first { it.product == product }, state.containers, state.measures)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = PADDING_SMALL,
                vertical = PADDING_EXTRA_SMALL
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.6f)
        ) {
            ProductNameUnityContainerInMyOrder(product)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.2f)
        ) {
            TextBody(
                text = quantity.toString(),
                textSize = TEXT_SIZE_LARGE,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = PADDING_ULTRA_SMALL)
            )
            TextBody(
                text = quantitySum,
                textSize = TEXT_SIZE_EXTRA_SMALL,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = PADDING_ZERO)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(0.2f)
        ) {
            val adjustTotal = if (product.container == ContainerType.COMMIT_MANGOES.value || product.container == ContainerType.COMMIT_AVOCADOS.value) {
                product.price
            } else { quantity * product.price }
            TextBody(
                text = "%.2f €".format(adjustTotal),
                textSize = TEXT_SIZE_SMALL,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = PADDING_ULTRA_SMALL)
            )
        }
    }
}

@Composable
fun NewOrderTopBar(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    val inCart = state.showShoppingCart
    val totalStr = String.format("%.2f €", state.productsOrderLineList.getAmount())
    ReguertaTopBar(
        topBarText = if (inCart) "Mi carrito - Total: $totalStr" else "Lista de productos",
        // Evita salidas accidentales: en carrito vuelve a Productos
        navActionClick = {
            if (inCart) onEvent(NewOrderEvent.HideShoppingCart)
            else onEvent(NewOrderEvent.GoOut)
        },
        actions = {
            // Botón único con ancho fijo: no cambia tamaño, toda el área es clicable
            InverseReguertaButton(
                onClick = {
                    if (inCart) onEvent(NewOrderEvent.HideShoppingCart)
                    else onEvent(NewOrderEvent.ShowShoppingCart)
                },
                content = {
                    Crossfade(targetState = inCart, label = "TopBarActionContent") { showCart ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (showCart) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBasket,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                TextBody(
                                    "Más",
                                    textSize = TEXT_SIZE_LARGE,
                                    textColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = PADDING_MEDIUM)
                                )
                            } else {
                                TextBody(
                                    "Ver",
                                    textSize = TEXT_SIZE_LARGE,
                                    textColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = PADDING_MEDIUM)
                                )
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                enabledButton = if (inCart) true else state.hasOrderLine,
                modifier = Modifier
                    // ancho fijo compacto (sin provocar wraps)
                    .widthIn(min = 148.dp, max = 148.dp)
                    .heightIn(min = 44.dp)
            )
        }
    )
}

@Composable
fun NewOrderBottomBar(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    // Altura fija para evitar saltos
    val barHeight = 72.dp
    val keyboard = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = barHeight)
            // Eleva la bottom bar por encima del teclado cuando aparece
            .imePadding()
            // (opcional) añade margen sobre la navigation bar cuando no hay teclado
            .navigationBarsPadding()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = PADDING_MEDIUM, topEnd = PADDING_MEDIUM)
            )
            .padding(horizontal = PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        // Animamos el cambio entre "Finalizar compra" y "Buscar producto"
        AnimatedContent(
            targetState = state.showShoppingCart,
            transitionSpec = {
                val duration = 450
                // Products -> Cart entra por la derecha; Cart -> Products entra por la izquierda
                val dir = if (targetState && !initialState) +1 else -1
                (slideInHorizontally(animationSpec = tween(duration)) { full -> full * dir } +
                 fadeIn(animationSpec = tween(duration)))
                    .togetherWith(
                        slideOutHorizontally(animationSpec = tween(duration)) { full -> -full * dir } +
                        fadeOut(animationSpec = tween(duration))
                    )
                    .using(SizeTransform(clip = true))
            },
            label = "BottomBarSwap"
        ) { inCart ->
            if (inCart) {
                // --- BOTÓN FINALIZAR ---
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ReguertaButton(
                        "Finalizar compra",
                        onClick = { onEvent(NewOrderEvent.PushOrder) },
                        enabledButton = state.hasOrderLine && !state.isOrdering,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = PADDING_EXTRA_SMALL,
                                horizontal = PADDING_MEDIUM
                            )
                    )
                    val hasPopup = state.showPopup != PopupType.NONE
                    if (state.isOrdering && !hasPopup) {
                        LoadingAnimation(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = PADDING_SMALL)
                        )
                    }
                }
            } else {
                // --- BUSCADOR (COLAPSADO / EXPANDIDO) ---
                AnimatedContent(
                    targetState = state.showSearch,
                    transitionSpec = {
                        val duration = 250
                        (fadeIn(animationSpec = tween(duration)) togetherWith fadeOut(animationSpec = tween(duration)))
                            .using(SizeTransform(clip = true))
                    },
                    label = "BottomSearchTransition"
                ) { expanded ->
                    if (!expanded) {
                        ReguertaButton(
                            textButton = "Buscar producto",
                            onClick = { onEvent(NewOrderEvent.ShowSearch) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = PADDING_EXTRA_SMALL,
                                    horizontal = PADDING_MEDIUM
                                )
                        )
                    } else {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { onEvent(NewOrderEvent.UpdateSearchQuery(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .focusRequester(focusRequester),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        onEvent(NewOrderEvent.HideSearch)
                                        keyboard?.hide()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Cerrar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            placeholder = { Text("Buscar producto") }
                        )
                        androidx.compose.runtime.LaunchedEffect(expanded) {
                            if (expanded) {
                                focusRequester.requestFocus()
                                keyboard?.show()
                            } else {
                                keyboard?.hide()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    // Log el valor de showShoppingCart directamente desde el estado del ViewModel
    Timber.i("SYNC_UI_STATE - showShoppingCart = ${state.showShoppingCart}")
    if (state.uiState == NewOrderUiMode.SELECT_PRODUCTS) {
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            topBar = { NewOrderTopBar(state, onEvent) },
            bottomBar = { NewOrderBottomBar(state, onEvent) }
        ) { paddingValues ->

            // 1) Frame único a pantalla completa
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .clipToBounds()
            ) {

                // 2) Pane actual según tu flag del estado
                val pane = if (state.showShoppingCart) Pane.Cart else Pane.Products

                // 3) Transición horizontal con dirección correcta
                AnimatedContent(
                    targetState = pane,
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart,
                    transitionSpec = {
                        // Products -> Cart entra por la derecha; Cart -> Products entra por la izquierda
                        val dir = if (targetState == Pane.Cart && initialState == Pane.Products) +1 else -1
                        val durationMs = 450

                        (slideInHorizontally(animationSpec = tween(durationMs)) { full -> full * dir }
                                + fadeIn(animationSpec = tween(durationMs)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(durationMs)) { full -> -full * dir } +
                                        fadeOut(animationSpec = tween(durationMs))
                            )
                            .using(SizeTransform(clip = true))
                    },
                    contentKey = { it }, // ayuda a Compose a reciclar correctamente
                    label = "NewOrderPaneSlide"
                ) { current ->
                    // 4) Cada vista ocupa exactamente el mismo marco
                    Box(Modifier.fillMaxSize()) {
                        when (current) {
                            Pane.Products -> GroupedProductsScreen(
                                groupedProducts = state.productsGroupedByCompany,
                                onEvent = onEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                            Pane.Cart -> ShoppingCartScreen(
                                productList = state.productsOrderLineList,
                                onEvent = onEvent,
                                showHeader = false
                            )
                        }
                    }
                }
            }
        }
    } else if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = Color.Red // para pruebas, visible
            )
        }
    } else {
        // (deja igual el resto de modos: EDIT_ORDER, SHOW_PREVIOUS_ORDER, ERROR)
        // No toques el bloque para EDIT_ORDER, SHOW_PREVIOUS_ORDER ni ERROR
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupedProductsScreen(
    groupedProducts: Map<String, List<Product>>,
    onEvent: (NewOrderEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (groupedProducts.isEmpty()) {
        // Mensaje de "sin resultados" mimetizado con la estética de NoOrderScreen, fijo arriba
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = PADDING_MEDIUM,
                end = PADDING_MEDIUM,
                top = PADDING_MEDIUM,
                bottom = PADDING_MEDIUM
            )
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ReguertaCard(
                        modifier = Modifier.width(330.dp),
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(PADDING_LARGE),
                                contentAlignment = Alignment.Center
                            ) {
                                TextTitle(
                                    text = "No hay productos que coincidan con la búsqueda.",
                                    textColor = MaterialTheme.colorScheme.error,
                                    textAlignment = TextAlign.Center
                                )
                            }
                        }
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            groupedProducts.forEach { (companyName, products) ->
                stickyHeader {
                    HeaderSectionText(
                        text = companyName
                    )
                }
                items(
                    count = products.size
                ) {
                    OrderProductItem(
                        product = products[it],
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingCartScreen(
    productList: List<ProductWithOrderLine>,
    onEvent: (NewOrderEvent) -> Unit,
    showHeader: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = PADDING_MEDIUM)
        ) {
            if (showHeader) {
                TextTitle(
                    text = "Mi carrito",
                    textSize = TEXT_TOP_BAR
                )
                Spacer(Modifier.weight(1f))
                AmountText(amount = productList.getAmount())
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PADDING_SMALL),
            contentPadding = PaddingValues(
                top = PADDING_SMALL,
                bottom = 72.dp
            )
        ) {
            items(
                count = productList.size
            ) {
                ShoppingCartOrderProductItem(
                    product = productList[it],
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
fun ShoppingCartOrderProductItem(
    product: ProductWithOrderLine,
    onEvent: (NewOrderEvent) -> Unit
) {
    ReguertaCard(
        modifier = Modifier
            .padding(PADDING_SMALL)
            .wrapContentSize(),
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(PADDING_SMALL)
            ) {
                ProductImage(
                    product,
                    imageSize = SIZE_96
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(PADDING_SMALL)
                        .fillMaxHeight()
                ) {
                    TextBody(
                        text = product.name,
                        textSize = TEXT_SIZE_LARGE,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = "${product.priceFormatted()} / ${product.getUnitType().singular}",
                        textSize = TEXT_SIZE_LARGE,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = PADDING_SMALL, end = PADDING_SMALL, top = PADDING_MEDIUM, bottom = PADDING_SMALL),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                    OrderQuantitySelector(
                        product,
                        onEvent
                    )
                }
            }
        }
    )
}

@Composable
private fun OrderProductItem(
    product: Product,
    onEvent: (NewOrderEvent) -> Unit
) {
    ReguertaCard(
        modifier = Modifier
            .padding(PADDING_SMALL)
            .wrapContentSize(),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HeaderItemProduct(
                    product,
                    onEvent
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = PADDING_SMALL)
                        .align(Alignment.Start)
                ) {
                    TextBody(
                        text = product.name,
                        textSize = TEXT_SIZE_LARGE,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = PADDING_ULTRA_SMALL)
                    )
                    TextBody(
                        text = product.description,
                        textSize = TEXT_SIZE_SMALL,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = PADDING_ULTRA_SMALL)
                    )
                    TextBody(
                        text = product.containerUnity(),
                        textSize = TEXT_SIZE_MEDIUM,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = PADDING_ULTRA_SMALL)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_SMALL)
                        .padding(bottom = PADDING_SMALL)
                ) {
                    TextBody(
                        text = "${product.priceFormatted()} / ${product.getUnitType().singular}",
                        textSize = TEXT_SIZE_LARGE,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    StockOrderText(
                        product.stock,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    )
}

@Composable
private fun HeaderItemProduct(
    product: Product,
    onEvent: (NewOrderEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(PADDING_SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(product)
        Spacer(Modifier.width(PADDING_EXTRA_LARGE))
        if (product is CommonProduct) {
            ButtonStartOrder(
                product.id,
                product.hasStock,
                onEvent
            )
        } else {
            OrderQuantitySelector(
                product as ProductWithOrderLine,
                onEvent
            )
        }
    }
}

@Composable
private fun ButtonStartOrder(
    productId: String,
    hasStock: Boolean,
    onEvent: (NewOrderEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onEvent(NewOrderEvent.StartOrder(productId))
        },
        enabled = hasStock,
        shape = RoundedCornerShape(16f),
        colors = ButtonDefaults.buttonColors(
            containerColor =  MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(PADDING_SMALL)
    ) {
        TextBody(
            "Añadir",
            textSize = TEXT_SIZE_LARGE,
            textColor = Color.White,
            modifier = Modifier
                .padding(horizontal = PADDING_EXTRA_SMALL)
        )
        Icon(
            imageVector = Icons.Filled.AddShoppingCart,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun OrderQuantitySelector(
    product: ProductWithOrderLine,
    onEvent: (NewOrderEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PADDING_SMALL),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextBody(
                text = product.getQuantity(),
                textSize = TEXT_SPECIAL,
                textColor = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(PADDING_EXTRA_SMALL))
            TextBody(
                text = product.getUnit(),
                textSize = TEXT_SIZE_LARGE,
                textColor = MaterialTheme.colorScheme.onSurface
            )
        }
        Row {
            ReguertaIconButton(
                iconButton = if (product.quantity == 1) Icons.Filled.Delete else Icons.Filled.Remove,
                onClick = {
                    onEvent(NewOrderEvent.MinusQuantityProduct(product.id))
                },
                contentColor = MaterialTheme.colorScheme.error
            )
            ReguertaIconButton(
                iconButton = Icons.Filled.Add,
                onClick = {
                    onEvent(NewOrderEvent.PlusQuantityProduct(product.id))
                },
                contentColor =  MaterialTheme.colorScheme.primary,
                enabledButton = product.stock > 0
                        && product.container != ContainerType.RESIGN.value
                        && product.container != ContainerType.COMMIT_MANGOES.value
                        && product.container != ContainerType.COMMIT_AVOCADOS.value
            )
        }
    }
}

@Composable
fun AreYouSureDeletePopup(
    onEvent: (NewOrderEvent) -> Unit,
    isDeletingOrder: Boolean
) {
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
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            if (isDeletingOrder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation(modifier = Modifier.size(80.dp))
                }
            } else {
                TextBody(
                    text = buildAnnotatedString {
                        append("Esta a punto de eliminar un pedido. \n")
                        append("Se restaurarán los stocks. \n")
                        append("Luego podrá realizar su pedido de nuevo.\n")
                    },
                    textSize = TEXT_SIZE_DLG_BODY,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center
                )
            }
        },
        title = {
            TextTitle(
                text = buildAnnotatedString {
                    append("Vas a eliminar un pedido\n")
                    append("¿Está seguro?")
                },
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            if (isDeletingOrder) {
                Spacer(Modifier.height(0.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PADDING_EXTRA_SMALL, vertical = PADDING_SMALL),
                    horizontalArrangement = Arrangement.spacedBy(PADDING_SMALL)
                ) {
                    InverseReguertaButton(
                        textButton = "Volver",
                        isSingleButton = false,
                        btnType = BtnType.ERROR,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onEvent(NewOrderEvent.HideDialog)
                        },
                        enabledButton = true
                    )
                    ReguertaButton(
                        textButton = "Aceptar",
                        isSingleButton = false,
                        btnType = BtnType.ERROR,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onEvent(NewOrderEvent.DeleteOrder)
                        },
                        enabledButton = true
                    )
                }
            }
        }
    )
}

@Composable
fun ConfirmPopup(
    title: AnnotatedString,
    body: AnnotatedString,
    confirmButton: () -> Unit,
    onEvent: (NewOrderEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background( MaterialTheme.colorScheme.primary.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint =  MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = body,
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = title,
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Aceptar",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    confirmButton()
                }
            )
        }
    )
}

@Composable
fun WrongPopup(
    title: AnnotatedString,
    body: AnnotatedString,
    confirmButton: () -> Unit,
    onEvent: (NewOrderEvent) -> Unit
) {
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
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = body,
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = title,
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Aceptar",
                isSingleButton = false,
                btnType = BtnType.ERROR,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    confirmButton()
                }
            )
        }
    )
}
