package com.reguerta.presentation.screen.new_order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.mapper.containerUnity
import com.reguerta.domain.model.mapper.getAmount
import com.reguerta.domain.model.mapper.priceFormatted
import com.reguerta.domain.model.received.OrderLineReceived
import com.reguerta.presentation.ALCAZAR
import com.reguerta.presentation.ALCAZAR_WITH_ORDER
import com.reguerta.presentation.composables.AmountText
import com.reguerta.presentation.composables.BtnType
import com.reguerta.presentation.composables.InverseReguertaButton
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCard
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.StockText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.image.ProductImage
import com.reguerta.presentation.screen.received_orders.OrderListByProduct
import com.reguerta.presentation.ui.Orange
import com.reguerta.presentation.ui.PADDING_EXTRA_LARGE
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_88
import com.reguerta.presentation.ui.SIZE_96
import com.reguerta.presentation.ui.SecondaryBackground
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_BODY
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_TITLE
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.TEXT_SPECIAL
import com.reguerta.presentation.ui.TEXT_TOP_BAR
import com.reguerta.presentation.ui.Text

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
            onEvent = viewModel::onEvent
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
    Screen {
        if (state.isExistOrder) {
            ExistingOrderScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        } else {
            NewOrderScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
fun ExistingOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mi pedido",
                navActionClick = {
                    onEvent(NewOrderEvent.GoOut)
                },
                actions = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PADDING_EXTRA_SMALL),
                        horizontalAlignment = Alignment.End
                    ) {
                        TextBody(
                            text = "¿Modificar?",
                            textColor = Orange,
                            modifier = Modifier
                                .wrapContentWidth(Alignment.End)
                        )
                        ReguertaIconButton(
                            onClick = {
                                onEvent(NewOrderEvent.ShowAreYouSureDeleteOrder)
                            },
                            iconButton = Icons.Filled.Delete,
                            contentColor = Orange,
                            modifier = Modifier.padding(horizontal = PADDING_MEDIUM)
                        )
                    }
                }
            )
        }
    ) {
        OrderListByProduct(
            state.ordersFromExistingOrder,
            modifier = Modifier
                .padding(it)
        )
    }
}

@Composable
fun NewOrderScreen(
    state: NewOrderState,
    onEvent: (NewOrderEvent) -> Unit
) {
    Scaffold(
        topBar = {
            if (state.showShoppingCart) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    InverseReguertaButton(
                        onClick = {
                            onEvent(NewOrderEvent.HideShoppingCart)
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier
                                    .padding(horizontal = PADDING_SMALL)
                                    .size(36.dp)
                            )
                            TextBody(
                                "Seguir comprando",
                                textSize = TEXT_SIZE_LARGE,
                                modifier = Modifier
                                    .padding(horizontal = PADDING_SMALL)
                            )
                        },
                        modifier = Modifier
                            .padding(
                                vertical = PADDING_SMALL,
                                horizontal = PADDING_MEDIUM
                            )
                    )
                }
            } else {
                ReguertaTopBar(
                    topBarText = "Lista de productos",
                    navActionClick = { onEvent(NewOrderEvent.GoOut) },
                    actions = {
                        InverseReguertaButton(
                            onClick = {
                                onEvent(NewOrderEvent.ShowShoppingCart)
                            },
                            content = {
                                TextBody(
                                    "Ver",
                                    textSize = TEXT_SIZE_MEDIUM,
                                    modifier = Modifier
                                        .padding(horizontal = PADDING_SMALL)
                                )
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = PrimaryColor
                                )
                            },
                            enabledButton = state.hasOrderLine
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (state.showShoppingCart) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomCenter)
                        .background(
                            color = SecondaryBackground,
                            shape = RoundedCornerShape(topStart = PADDING_MEDIUM, topEnd = PADDING_MEDIUM)
                        )
                        .padding(PADDING_SMALL)
                ) {
                    ReguertaButton(
                        "Finalizar compra",
                        onClick = {
                            onEvent(NewOrderEvent.PushOrder)
                        },
                        enabledButton = state.hasOrderLine,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = PADDING_SMALL,
                                horizontal = PADDING_MEDIUM
                            )
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            if (!state.isLoading) {
                AnimatedVisibility(
                    visible = state.showShoppingCart
                ) {
                    ShoppingCartScreen(
                        state.productsOrderLineList,
                        onEvent
                    )
                }
                AnimatedVisibility(
                    visible = !state.showShoppingCart
                ) {
                    AvailableProductsScreen(
                        state.availableCommonProducts,
                        onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingCartScreen(
    productList: List<ProductWithOrderLine>,
    onEvent: (NewOrderEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = PADDING_MEDIUM)
        ) {
            TextTitle(
                text = "Mi carrito",
                textSize = TEXT_TOP_BAR
            )
            AmountText(
                amount = productList.getAmount()
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(PADDING_SMALL)
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
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )

                    TextBody(
                        text = "${product.priceFormatted()} / ${product.getUnitType().singular}",
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = PADDING_SMALL, end = PADDING_SMALL, top = PADDING_MEDIUM, bottom = PADDING_SMALL),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )

                    OrderQuantitySelector(
                        product,
                        onEvent,
                        //modifier = Modifier.padding(vertical = PADDING_EXTRA_SMALL)
                    )
                }
            }
        }
    )
}

@Composable
private fun AvailableProductsScreen(
    availableProductsList: List<Product>,
    onEvent: (NewOrderEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_SMALL)
    ) {
        items(
            count = availableProductsList.size
        ) {
            OrderProductItem(
                product = availableProductsList[it],
                onEvent = onEvent
            )
        }
    }
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
            HeaderItemProduct(
                product,
                onEvent
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(PADDING_SMALL)
                        .fillMaxHeight()
                ) {
                    TextBody(
                        text = product.name,
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = product.description,
                        textSize = TEXT_SIZE_SMALL,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = product.containerUnity(),// Por separado (dif tam)
                        textSize = TEXT_SIZE_MEDIUM,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = "${product.priceFormatted()} / ${product.container}",
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .padding(PADDING_SMALL)
                        .fillMaxHeight()
                ) {
                    StockText(
                        product.stock,
                        textSize = TEXT_SIZE_LARGE,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_SMALL)
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
            containerColor = PrimaryColor,
            disabledContainerColor = Color.Gray.copy(alpha = 0.15f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(PADDING_SMALL)
    ) {
        TextBody(
            "Añadir al carro",
            textSize = TEXT_SIZE_MEDIUM,
            textColor = Color.White,
            modifier = Modifier
                .padding(horizontal = PADDING_EXTRA_SMALL)
        )
        /*TextRegular(
            text = "Añadir al carro",
            textSize = TEXT_SIZE_LARGE,
            textColor = Color.White,
            modifier = Modifier
                .padding(PADDING_EXTRA_SMALL)
        )
        Spacer(Modifier.width(PADDING_EXTRA_SMALL))*/
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
                textColor = Text
            )
            Spacer(modifier = Modifier.width(PADDING_EXTRA_SMALL))
            TextBody(
                text = product.getUnit(),
                textSize = TEXT_SIZE_LARGE,
                textColor = Text
            )
        }
        Row {
            ReguertaIconButton(
                iconButton = if (product.quantity == 1) Icons.Filled.Delete else Icons.Filled.Remove,
                onClick = {
                    onEvent(NewOrderEvent.MinusQuantityProduct(product.id))
                },
                contentColor = Orange
            )
            ReguertaIconButton(
                iconButton = Icons.Filled.Add,
                onClick = {
                    onEvent(NewOrderEvent.PlusQuantityProduct(product.id))
                },
                contentColor = PrimaryColor,
                enabledButton = product.stock > 0
            )
        }
    }
}

@Preview
@Composable
fun NewOrderScreenPreview() {
    Screen {
        NewOrderScreen(
            state = NewOrderState(
                isLoading = false,
                hasOrderLine = true,
                availableCommonProducts = listOf(
                    ALCAZAR,
                    ALCAZAR.copy(
                        stock = 0
                    ),
                    ALCAZAR_WITH_ORDER
                )
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
fun ShoppingCartScreenPreview() {
    Screen {
        NewOrderScreen(
            state = NewOrderState(
                isLoading = false,
                hasOrderLine = true,
                showShoppingCart = true,
                availableCommonProducts = listOf(
                    ALCAZAR,
                    ALCAZAR_WITH_ORDER
                ),
                productsOrderLineList = listOf(
                    ALCAZAR_WITH_ORDER.copy(
                        orderLine = ALCAZAR_WITH_ORDER.orderLine.copy(
                            quantity = 2
                        )
                    )
                )
            ),
            onEvent = {}
        )
    }
}

@Composable
fun AreYouSureDeletePopup(
    onEvent: (NewOrderEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SIZE_88)
                    .background(Orange.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = Orange,
                    modifier = Modifier
                        .size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = buildAnnotatedString {
                    append("Esta a punto de eliminar un pedido. \n")
                    append("Se restaurarán los stocks. \n")
                    append("Luego podrá realizar su pedido de nuevo.\n")
                },
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = buildAnnotatedString {
                    append("Vas a eliminar un pedido\n")
                    append("¿Está seguro?")
                },
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
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
                    }
                )
                ReguertaButton(
                    textButton = "Aceptar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(NewOrderEvent.DeleteOrder)
                    }
                )
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
                    .background(PrimaryColor.copy(alpha = 0.2F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = PrimaryColor,
                    modifier = Modifier.size(SIZE_48)
                )
            }
        },
        onDismissRequest = { onEvent(NewOrderEvent.HideDialog) },
        text = {
            TextBody(
                text = body,
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = title,
                textSize = TEXT_SIZE_DLG_TITLE,
                textColor = Text,
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

@Preview
@Composable
fun ExistingOrderScreenPreviewPreview() {
    Screen {
        ExistingOrderScreen(
            state = NewOrderState(
                isLoading = false,
                hasOrderLine = true,
                showShoppingCart = true,
                availableCommonProducts = listOf(
                    ALCAZAR,
                    ALCAZAR_WITH_ORDER
                ),
                productsOrderLineList = listOf(
                    ALCAZAR_WITH_ORDER.copy(
                        orderLine = ALCAZAR_WITH_ORDER.orderLine.copy(
                            quantity = 2
                        )
                    )
                ),
                isExistOrder = true,
                ordersFromExistingOrder = mapOf(
                    ALCAZAR to listOf(
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

@Preview
@Composable
fun ShowAreYouSureDeleteOrderPreview() {
    Screen {
        AreYouSureDeletePopup(
            onEvent = {

            }
        )
    }
}