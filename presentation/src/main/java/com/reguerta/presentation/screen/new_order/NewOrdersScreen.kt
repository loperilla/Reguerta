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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.reguerta.presentation.composables.ProductImage
import com.reguerta.presentation.composables.ProductNameUnityContainerInMyOrder
import com.reguerta.presentation.getQuantitySum
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.navigation.Routes
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoOrderScreen(
    onEvent: (NewOrderEvent) -> Unit
) {
    ReguertaScaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mi último pedido",
                navActionClick = {
                    onEvent(NewOrderEvent.GoOut)
                }
            )
        }
    ) {
        ReguertaCard(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = Dimens.Spacing.md, vertical = Dimens.Spacing.md),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    TextTitle(
                        text = "No hay ningún pedido registrado.",
                        textColor = MaterialTheme.colorScheme.onErrorContainer,
                        textAlignment = TextAlign.Center
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    val grandTotal = state.orderLinesByCompanyName.values.sumOf { it.getDblAmount() }

    ReguertaScaffold(
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
                            modifier = Modifier.padding(end = Dimens.Spacing.sm)
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
                    .padding(Dimens.Spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                TextTitle(
                    text = "Suma total pedido: %.2f €".format(grandTotal),
                    textSize = MaterialTheme.typography.titleLarge.fontSize,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    val grandTotal = state.orderLinesByCompanyName.values.sumOf { it.getDblAmount() }

    ReguertaScaffold(
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
                    .padding(Dimens.Spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                TextTitle(
                    text = "Suma total pedido: %.2f €".format(grandTotal),
                    textSize = MaterialTheme.typography.titleLarge.fontSize,
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
            .padding(Dimens.Spacing.sm)
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
            horizontal = Dimens.Spacing.sm,
            vertical = Dimens.Spacing.sm
        ),
        content = {
            TextBody(
                text = companyName,
                textSize = MaterialTheme.typography.titleMedium.fontSize,
                textColor =  MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(
                        top = Dimens.Spacing.sm,
                        bottom = Dimens.Spacing.xs,
                        start = Dimens.Spacing.md
                    ),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.Spacing.zero))

            orderLines.forEach {
                ProductLine(
                    quantity = it.quantity,
                    subtotal = it.subtotal,
                    product = it.product,
                    orderLines = orderLines,
                    state = state
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.Spacing.xs))

            TextBody(
                text = "Total: ${String.format("%.2f", orderLines.getDblAmount())}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = Dimens.Spacing.sm,
                        top = Dimens.Spacing.xs,
                        end = Dimens.Spacing.md
                    ),
                textColor = MaterialTheme.colorScheme.error,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
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
                horizontal = Dimens.Spacing.sm,
                vertical = Dimens.Spacing.xs
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
                textSize = MaterialTheme.typography.bodyLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.Spacing.xxs)
            )
            TextBody(
                text = quantitySum,
                textSize = MaterialTheme.typography.bodySmall.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = Dimens.Spacing.zero)
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
                textSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = Dimens.Spacing.xxs)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                                    textSize = MaterialTheme.typography.labelLarge.fontSize,
                                    textColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = Dimens.Spacing.md)
                                )
                            } else {
                                TextBody(
                                    "Ver",
                                    textSize = MaterialTheme.typography.labelLarge.fontSize,
                                    textColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = Dimens.Spacing.md)
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
                shape = RoundedCornerShape(topStart = Dimens.Spacing.md, topEnd = Dimens.Spacing.md)
            )
            .padding(horizontal = Dimens.Spacing.sm),
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
                                vertical = Dimens.Spacing.xs,
                                horizontal = Dimens.Spacing.md
                            )
                    )
                    val hasPopup = state.showPopup != PopupType.NONE
                    if (state.isOrdering && !hasPopup) {
                        LoadingAnimation(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = Dimens.Spacing.sm)
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
                                    vertical = Dimens.Spacing.xs,
                                    horizontal = Dimens.Spacing.md
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    // Log el valor de showShoppingCart directamente desde el estado del ViewModel
    Timber.i("SYNC_UI_STATE - showShoppingCart = ${state.showShoppingCart}")
    if (state.uiState == NewOrderUiMode.SELECT_PRODUCTS) {
        ReguertaScaffold(
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
                start = Dimens.Spacing.md,
                end = Dimens.Spacing.md,
                top = Dimens.Spacing.md,
                bottom = Dimens.Spacing.md
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
                                    .padding(Dimens.Spacing.lg),
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
                .padding(horizontal = Dimens.Spacing.md)
        ) {
            if (showHeader) {
                TextTitle(
                    text = "Mi carrito",
                    textSize = MaterialTheme.typography.headlineSmall.fontSize
                )
                Spacer(Modifier.weight(1f))
                AmountText(amount = productList.getAmount())
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.Spacing.sm),
            contentPadding = PaddingValues(
                top = Dimens.Spacing.sm,
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
            .padding(Dimens.Spacing.sm)
            .wrapContentSize(),
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(Dimens.Spacing.sm)
            ) {
                ProductImage(
                    product,
                    imageSize = Dimens.Size.dp96
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(Dimens.Spacing.sm)
                        .fillMaxHeight()
                ) {
                    TextBody(
                        text = product.name,
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.xs)
                    )
                    TextBody(
                        text = "${product.priceFormatted()} / ${product.getUnitType().singular}",
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.xs)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = Dimens.Spacing.sm, end = Dimens.Spacing.sm, top = Dimens.Spacing.md, bottom = Dimens.Spacing.sm),
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
            .padding(Dimens.Spacing.sm)
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
                        .padding(horizontal = Dimens.Spacing.sm)
                        .align(Alignment.Start)
                ) {
                    TextBody(
                        text = product.name,
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = Dimens.Spacing.xxs)
                    )
                    TextBody(
                        text = product.description,
                        textSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = Dimens.Spacing.xxs)
                    )
                    TextBody(
                        text = product.containerUnity(),
                        textSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = Dimens.Spacing.xxs)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Spacing.sm)
                        .padding(bottom = Dimens.Spacing.sm)
                ) {
                    TextBody(
                        text = "${product.priceFormatted()} / ${product.getUnitType().singular}",
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
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
            .padding(Dimens.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(product)
        Spacer(Modifier.width(Dimens.Spacing.xl))
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
            .padding(Dimens.Spacing.sm)
    ) {
        TextBody(
            "Añadir",
            textSize = MaterialTheme.typography.labelLarge.fontSize,
            textColor = Color.White,
            modifier = Modifier
                .padding(horizontal = Dimens.Spacing.xs)
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
            .padding(horizontal = Dimens.Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextBody(
                text = product.getQuantity(),
                textSize = MaterialTheme.typography.headlineSmall.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(Dimens.Spacing.xs))
            TextBody(
                text = product.getUnit(),
                textSize = MaterialTheme.typography.bodyLarge.fontSize,
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
                    .size(Dimens.Size.dp88)
                    .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(Dimens.Size.dp48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            if (isDeletingOrder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.Size.dp96),
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
                    textSize = MaterialTheme.typography.bodyMedium.fontSize,
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
                textSize = MaterialTheme.typography.titleLarge.fontSize,
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
                        .padding(horizontal = Dimens.Spacing.xs, vertical = Dimens.Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
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
                    .size(Dimens.Size.dp88)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(Dimens.Size.dp48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = body,
                textSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = title,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
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
                    .size(Dimens.Size.dp88)
                    .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(Dimens.Size.dp48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = body,
                textSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = title,
                textSize = MaterialTheme.typography.titleLarge.fontSize,
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
