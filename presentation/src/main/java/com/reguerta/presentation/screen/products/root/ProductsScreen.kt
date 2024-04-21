package com.reguerta.presentation.screen.products.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.priceFormatted
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
import com.reguerta.presentation.ui.Orange
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_88
import com.reguerta.presentation.ui.SecondaryBackground
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_BODY
import com.reguerta.presentation.ui.TEXT_SIZE_DLG_TITLE
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.products
 * Created By Manuel Lopera on 25/2/24 at 11:41
 * All rights reserved 2024
 */

@Composable
fun productScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel: ProductsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo(Routes.HOME.route)
        return
    }
    Screen {
        ProductsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navigateTo
        )
    }
}

@Composable
private fun ProductsScreen(
    state: ProductsState,
    onEvent: (ProductsEvent) -> Unit,
    navigateTo: (String) -> Unit
) {
    AnimatedVisibility(
        state.showAreYouSureDialog
    ) {
        AreYouSureDialog(
            onEvent = onEvent
        )
    }
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mis productos",
                navActionClick = { onEvent(ProductsEvent.GoOut) }
            )
        },
        bottomBar = {
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
                    "Añadir nuevo producto",
                    onClick = { navigateTo(Routes.PRODUCTS.ADD.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = PADDING_SMALL,
                            horizontal = PADDING_MEDIUM
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            if (!state.isLoading) {
                ProductListScreen(
                    state.commonProducts,
                    onEvent,
                    navigateTo
                )
            }
        }
    }
}

@Composable
private fun ProductListScreen(
    products: List<CommonProduct>,
    onEvent: (ProductsEvent) -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(PADDING_SMALL)
    ) {
        items(
            count = products.size
        ) {
            ProductItem(
                product = products[it],
                onEvent = onEvent,
                navigateTo
            )
        }
    }
}

@Composable
private fun ProductItem(
    product: CommonProduct,
    onEvent: (ProductsEvent) -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ReguertaCard(
        modifier = modifier
            .padding(PADDING_SMALL)
            .wrapContentSize(),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    ProductImage(product)
                    Row(
                        modifier = Modifier
                            .padding(PADDING_SMALL),
                        horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReguertaIconButton(
                            iconButton = Icons.Filled.Edit,
                            onClick = { navigateTo(Routes.PRODUCTS.EDIT.createRoute(product.id)) },
                            contentColor = PrimaryColor
                        )
                        ReguertaIconButton(
                            iconButton = Icons.Filled.Delete,
                            onClick = { onEvent(ProductsEvent.ShowAreYouSureDialog(product.id)) },
                            contentColor = Orange
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(PADDING_EXTRA_SMALL)
                        .fillMaxWidth()
                ) {
                    TextBody(
                        text = product.name,
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text,
                        modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = product.description,
                        textSize = TEXT_SIZE_SMALL,
                        textColor = Text,
                        modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBody(
                            text = product.priceFormatted(),
                            textSize = TEXT_SIZE_LARGE,
                            textColor = Text,
                            modifier = Modifier.padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                        )
                        StockText(
                            product.stock,
                            textSize = TEXT_SIZE_LARGE,
                            modifier = Modifier.padding(end = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                        )
                    }
                }
            }
        }
    )
}


@Composable
private fun AreYouSureDialog(
    onEvent: (ProductsEvent) -> Unit
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
        onDismissRequest = { onEvent(ProductsEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar un producto.\nEsta acción no se podrá deshacer.",
                textSize = TEXT_SIZE_DLG_BODY,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Vas a eliminar un producto\n¿Estás seguro?",
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
                    textButton = "Cancelar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(ProductsEvent.HideAreYouSureDialog)
                    }
                )
                ReguertaButton(
                    textButton = "Aceptar",
                    isSingleButton = false,
                    btnType = BtnType.ERROR,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(ProductsEvent.ConfirmDeleteProduct)
                    }
                )
            }
        },
        dismissButton = { /* No se usa  */ }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductItemPreview() {
    Screen {
        ProductsScreen(
            state = ProductsState(
                isLoading = false,
                commonProducts = listOf(
                    CommonProduct(
                        name = "Product 1",
                        description = "Description 1",
                        container = "Container 1",
                        price = 1.0f,
                        available = true,
                        companyName = "Company 1",
                        imageUrl = "",
                        stock = 1,
                        quantityContainer = 1,
                        quantityWeight = 1,
                        unity = "Unity 1"
                    )
                )
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}
