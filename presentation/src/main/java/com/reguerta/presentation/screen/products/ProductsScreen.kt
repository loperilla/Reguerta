package com.reguerta.presentation.screen.products

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.domain.model.Product
import com.reguerta.presentation.composables.ImageUrl
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaIconButton
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SecondaryBackground
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
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    TextTitle(
                        text = "Mis productos",
                        textSize = 26.sp,
                        textColor = Text
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ProductsEvent.GoOut) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomCenter)
                    .background(
                        color = SecondaryBackground,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(8.dp)
            ) {
                ReguertaButton(
                    "Añadir nuevo producto",
                    onClick = { navigateTo(Routes.USERS.ADD.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
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
            .padding(8.dp)
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
    val colors = CardDefaults.cardColors(
        containerColor = SecondaryBackground
    )
    Card(
        modifier = modifier
            .padding(8.dp)
            .wrapContentSize(),
        colors = colors
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            ImageUrl(
                imageUrl = product.imageUrl,
                name = product.name,
                modifier = Modifier
                    .padding(8.dp)
                    .size(96.dp)
            )
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReguertaIconButton(
                    iconButton = Icons.Filled.Edit,
                    onClick = {
//                        navigateTo(Routes.USERS.EDIT.createRoute(user.id))
                    },
                    contentColor = PrimaryColor
                )
                ReguertaIconButton(
                    iconButton = Icons.Filled.Delete,
                    onClick = {
                        onEvent(ProductsEvent.DeleteProduct(product.id))
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
                    .padding(8.dp)
                    .fillMaxHeight()
            ) {
                TextBody(
                    text = product.name,
                    textSize = 18.sp,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp)
                )
                TextBody(
                    text = product.description,
                    textSize = 16.sp,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp)
                )
                TextBody(
                    text = "${product.price} €",
                    textSize = 16.sp,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight()
            ) {
                TextBody(
                    text = "Stock: ${product.stock}",
                    textSize = 16.sp,
                    textColor = Text,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
                )
            }
        }
    }
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
                        price = 1.0,
                        available = true,
                        companyName = "Company 1",
                        imageUrl = "",
                        stock = 1,
                        quantityContainer = 1,
                        quantityWeight = 1.0,
                        unity = "Unity 1"
                    )
                )
            ),
            onEvent = {},
            navigateTo = {}
        )
    }
}
