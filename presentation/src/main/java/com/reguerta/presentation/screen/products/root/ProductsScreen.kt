package com.reguerta.presentation.screen.products.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.Product
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.ImageUrl
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
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_48
import com.reguerta.presentation.ui.SIZE_96
import com.reguerta.presentation.ui.SecondaryBackground
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

@OptIn(ExperimentalMaterial3Api::class)
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
                    state.products,
                    onEvent,
                    navigateTo
                )
            }
        }
    }
}

@Composable
fun ProductListScreen(
    products: List<Product>,
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
fun ProductItem(
    product: Product,
    onEvent: (ProductsEvent) -> Unit,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ReguertaCard(
        modifier = modifier
            .padding(PADDING_SMALL)
            .wrapContentSize(),
        content = {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                if (product.imageUrl.isEmpty()) {
                    Image(
                        painter = painterResource(id = R.mipmap.product_no_available),
                        contentDescription = product.name,
                        modifier = Modifier
                            .padding(PADDING_SMALL)
                            .size(SIZE_96)
                    )
                } else {
                    ImageUrl(
                        imageUrl = product.imageUrl,
                        name = product.name,
                        modifier = Modifier
                            .padding(PADDING_SMALL)
                            .size(SIZE_96)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(PADDING_SMALL),
                    horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReguertaIconButton(
                        iconButton = Icons.Filled.Edit,
                        onClick = {
                            navigateTo(Routes.PRODUCTS.EDIT.createRoute(product.id))
                        },
                        contentColor = PrimaryColor
                    )
                    ReguertaIconButton(
                        iconButton = Icons.Filled.Delete,
                        onClick = {
                            onEvent(ProductsEvent.ShowAreYouSureDialog(product.id))
                        },
                        contentColor = Color.Red
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
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
                        textSize = TEXT_SIZE_LARGE,
                        textColor = Text,
                        modifier = Modifier
                            .padding(start = PADDING_MEDIUM, top = PADDING_EXTRA_SMALL)
                    )
                    TextBody(
                        text = "${"%.2f".format(product.price)} €",
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
private fun AreYouSureDialog(
    onEvent: (ProductsEvent) -> Unit
) {
    ReguertaAlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "ExitApp",
                tint = PrimaryColor,
                modifier = Modifier
                    .size(SIZE_48)
            )
        },
        onDismissRequest = { onEvent(ProductsEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar un producto. Esta acción no se podrá deshacer",
                textSize = TEXT_SIZE_SMALL,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Vas a eliminar un producto ¿Estás seguro?",
                textSize = TEXT_SIZE_LARGE,
                textColor = Text,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            ReguertaButton(
                textButton = "Aceptar",
                onClick = {
                    onEvent(ProductsEvent.ConfirmDeleteProduct)
                }
            )
        },
        dismissButton = {
            InverseReguertaButton(
                textButton = "Cancelar",
                onClick = {
                    onEvent(ProductsEvent.HideAreYouSureDialog)
                }
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductItemPreview() {
    Screen {
        ProductsScreen(
            state = ProductsState(
                isLoading = false,
                products = listOf(
                    Product(
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
