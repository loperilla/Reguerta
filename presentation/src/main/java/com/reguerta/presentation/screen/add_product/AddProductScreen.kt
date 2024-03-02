package com.reguerta.presentation.screen.add_product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reguerta.presentation.R
import com.reguerta.presentation.composables.DropDownItem
import com.reguerta.presentation.composables.DropdownSelectable
import com.reguerta.presentation.composables.ImageUrl
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaCounter
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.SecondaryTextReguertaInput
import com.reguerta.presentation.composables.StockText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.composables.TextTitle
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_product
 * Created By Manuel Lopera on 1/3/24 at 17:02
 * All rights reserved 2024
 */

@Composable
fun addProductScreen(
    navigateTo: (String) -> Unit
) {
    val viewModel = hiltViewModel<AddProductViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo(Routes.PRODUCTS.route)
        return
    }
    Screen {
        AddProductScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductScreen(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { TextTitle(text = "Add Product", textSize = 26.sp, textColor = Text) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddProductEvent.GoOut) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            HeaderAddProductForm(
                state,
                onEvent
            )

            TextReguertaInput(
                text = state.name,
                onTextChange = { newName ->
                    onEvent(AddProductEvent.OnNameChanged(newName))
                },
                labelText = "Nombre del producto",
                placeholderText = "Pulsa para escribir",
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            TextReguertaInput(
                text = state.description,
                onTextChange = { newDescription ->
                    onEvent(AddProductEvent.OnDescriptionChanged(newDescription))
                },
                imeAction = ImeAction.Next,
                labelText = "Descripción del producto",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            UnityAndContainer(
                state = state,
                onEvent = onEvent
            )

            TextReguertaInput(
                text = state.price,
                onTextChange = { newPrice ->
                    onEvent(AddProductEvent.OnPriceChanged(newPrice))
                },
                imeAction = ImeAction.Next,
                labelText = "Precio",
                placeholderText = "Pulsa para escribir",
                keyboardType = KeyboardType.Number,
                suffixValue = "€",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            ReguertaButton(
                textButton = "Añadir producto",
                enabledButton = state.isButtonEnabled,
                onClick = { onEvent(AddProductEvent.AddProduct) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HeaderAddProductForm(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (state.bitmap != null) {
            ImageUrl(
                imageUrl = state.bitmap.toString(),
                name = state.name,
                modifier = Modifier
                    .padding(8.dp)
                    .size(96.dp)
            )
        } else {
            Image(
                painter = painterResource(R.mipmap.product_no_available),
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(8.dp)
                    .size(96.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextBody(
                    text = "Disponible",
                    textSize = 16.sp,
                    textColor = Text
                )
                ReguertaCheckBox(
                    isChecked = state.isAvailable,
                    onCheckedChange = { newValue ->
                        onEvent(AddProductEvent.OnAvailableChanges(newValue))
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                StockText(
                    stockCount = state.stock,
                    textSize = 22.sp
                )

                ReguertaCounter(
                    state.stock,
                    onMinusButtonClicked = {
                        onEvent(AddProductEvent.OnStockChanged(state.stock.minus(1)))
                    },
                    onPlusButtonClicked = {
                        onEvent(AddProductEvent.OnStockChanged(state.stock.plus(1)))
                    }
                )
            }
        }
    }
}

@Composable
fun UnityAndContainer(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
    ) {
        SecondaryTextReguertaInput(
            text = "${state.containerValue}",
            onTextChange = { newContainer ->
                onEvent(AddProductEvent.OnContainerValueChanges(newContainer))
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.NumberPassword,
            placeholderText = "Pulsa para escribir",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(8.dp)
        )

        DropdownSelectable(
            currentSelected = state.containerType.ifEmpty {
                "Selecciona envase"
            },
            dropdownItems = state.containers.map {
                DropDownItem(text = it.name)
            },
            onItemClick = {
                onEvent(AddProductEvent.OnContainerTypeChanges(it.text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
    ) {
        SecondaryTextReguertaInput(
            text = "${state.measureValue}",
            onTextChange = { newMeasure ->
                onEvent(AddProductEvent.OnMeasuresValueChanges(newMeasure))
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.NumberPassword,
            placeholderText = "Pulsa para escribir",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(8.dp)
        )

        DropdownSelectable(
            currentSelected = state.measureType.ifEmpty {
                "Selecciona unidad"
            },
            dropdownItems = state.measures.map {
                DropDownItem(text = it.name)
            },
            onItemClick = {
                onEvent(AddProductEvent.OnMeasuresTypeChanges(it.text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
fun AddProductScreenPreview() {
    Screen {
        AddProductScreen(
            state = AddProductState(),
            onEvent = {}
        )
    }
}
