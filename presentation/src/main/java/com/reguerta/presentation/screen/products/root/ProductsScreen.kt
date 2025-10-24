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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.reguerta.presentation.composables.StockProductText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.composables.ProductImage
import com.reguerta.presentation.composables.ReguertaScaffold
import com.reguerta.presentation.ui.Dimens
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import com.reguerta.presentation.composables.ReguertaFullButton
import com.reguerta.presentation.navigation.Routes

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
    ReguertaScaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Mis productos",
                navActionClick = { onEvent(ProductsEvent.GoOut) }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = Dimens.Radius.lg, topEnd = Dimens.Radius.lg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Dimens.Spacing.sm, vertical = Dimens.Spacing.sm)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ReguertaFullButton(
                            "Añadir producto",
                            onClick = { navigateTo(Routes.PRODUCTS.ADD.route) }
                        )
                    }
                }
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
            .padding(Dimens.Spacing.sm)
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
            .padding(Dimens.Spacing.sm)
            .wrapContentSize(),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier.padding(Dimens.Spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReguertaIconButton(
                            iconButton = Icons.Filled.Edit,
                            onClick = { navigateTo(Routes.PRODUCTS.EDIT.createRoute(product.id)) },
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                        ReguertaIconButton(
                            iconButton = Icons.Filled.Delete,
                            onClick = { onEvent(ProductsEvent.ShowAreYouSureDialog(product.id)) },
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(Dimens.Spacing.xs)
                        .fillMaxWidth()
                ) {
                    TextBody(
                        text = product.name,
                        textSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.xs)
                    )
                    TextBody(
                        text = product.description,
                        textSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.xs)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBody(
                            text = product.priceFormatted(),
                            textSize = MaterialTheme.typography.bodyLarge.fontSize,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = Dimens.Spacing.md, top = Dimens.Spacing.xs)
                        )
                        StockProductText(
                            product.stock,
                            textSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = Dimens.Spacing.md, top = Dimens.Spacing.xs)
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
        onDismissRequest = { onEvent(ProductsEvent.HideAreYouSureDialog) },
        text = {
            TextBody(
                text = "Estás a punto de eliminar un producto.\nEsta acción no se podrá deshacer.",
                textSize = MaterialTheme.typography.bodyMedium.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        title = {
            TextTitle(
                text = "Vas a eliminar un producto\n¿Estás seguro?",
                textSize = MaterialTheme.typography.titleLarge.fontSize,
                textColor = MaterialTheme.colorScheme.onSurface,
                textAlignment = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Spacing.xs, vertical = Dimens.Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
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
