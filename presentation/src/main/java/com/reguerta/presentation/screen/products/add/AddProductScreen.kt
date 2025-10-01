package com.reguerta.presentation.screen.products.add

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.reguerta.presentation.R
import com.reguerta.presentation.checkRationalPermission
import com.reguerta.presentation.checkStoragePermission
import com.reguerta.presentation.composables.CustomTextField
import com.reguerta.presentation.composables.DropDownItem
import com.reguerta.presentation.composables.DropdownSelectable
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaCounter
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.StockProductText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.getStoragePermissionBySdk
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.Routes
import com.reguerta.presentation.ui.SIZE_96
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.uriToBitmap
import kotlinx.coroutines.launch

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

@Composable
private fun AddProductScreen(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    Scaffold(
        topBar = {
            ReguertaTopBar(
                topBarText = "Añadir Producto",
                navActionClick = { onEvent(AddProductEvent.GoOut) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
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
                labelText = "NOMBRE DEL PRODUCTO",
                placeholderText = "Pulsa para escribir",
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .padding(horizontal = PADDING_MEDIUM)
                    .fillMaxWidth()
            )

            TextReguertaInput(
                text = state.description,
                onTextChange = { newDescription ->
                    onEvent(AddProductEvent.OnDescriptionChanged(newDescription))
                },
                imeAction = ImeAction.Next,
                labelText = "DESCRIPCIÓN DEL PRODUCTO",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .padding(horizontal = PADDING_MEDIUM)
                    .fillMaxWidth()
            )

            UnityAndContainer(
                state = state,
                onEvent = onEvent
            )

            TextReguertaInput(
                text = state.price,
                onTextChange = { newPrice ->
                    onEvent(
                        AddProductEvent.OnPriceChanged(newPrice)
                    )
                },
                imeAction = ImeAction.Next,
                labelText = "PRECIO EN EUROS",
                placeholderText = "Pulsa para escribir",
                keyboardType = KeyboardType.Number,
                suffixValue = "€",
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .padding(horizontal = PADDING_MEDIUM)
                    .fillMaxWidth()
            )

            ReguertaButton(
                textButton = "Añadir producto",
                enabledButton = state.isButtonEnabled,
                onClick = { onEvent(AddProductEvent.AddProduct) },
                modifier = Modifier
                    .padding(PADDING_MEDIUM)
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
private fun HeaderAddProductForm(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var photoUri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        photoUri = uri
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (map.all { entry -> entry.value }) {
            launcher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
            return@rememberLauncherForActivityResult
        }
        map.entries.forEach {
            if (checkRationalPermission(context, it.key)) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }
    Row(
        modifier = modifier
            .padding(PADDING_SMALL)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (photoUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(context)
                    .data(data = photoUri)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .size(SIZE_96)
                    .clickable {
                        if (checkStoragePermission(context)) {
                            launcher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            return@clickable
                        }
                        permissionLauncher.launch(
                            getStoragePermissionBySdk()
                        )
                    }
            )
            uriToBitmap(
                context,
                photoUri!!
            )?.let {
                onEvent(
                    AddProductEvent.OnImageSelected(
                        it
                    )
                )
            }
        } else {
            Image(
                painter = painterResource(R.mipmap.product_no_available),
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .size(SIZE_96)
                    .clickable {
                        if (checkStoragePermission(context)) {
                            launcher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            return@clickable
                        }
                        permissionLauncher.launch(
                            getStoragePermissionBySdk()
                        )
                    }
            )
        }
        Column(
            modifier = Modifier
                .padding(PADDING_SMALL)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextBody(
                    text = "Disponible",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
                ReguertaCheckBox(
                    isChecked = state.isAvailable,
                    onCheckedChange = { newValue ->
                        onEvent(AddProductEvent.OnAvailableChanges(newValue))
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PADDING_MEDIUM, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StockProductText(
                    stockCount = state.stock,
                    textSize = TEXT_SIZE_LARGE
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
private fun UnityAndContainer(
    state: AddProductState,
    onEvent: (AddProductEvent) -> Unit
) {
    val containerDropdownItems = if ((state.containerValue.toIntOrNull() ?: 0) > 1) {
        state.containers.map { DropDownItem(text = it.plural) }
    } else {
        state.containers.map { DropDownItem(text = it.name) }
    }

    val measureDropdownItems = if ((state.measureValue.toIntOrNull() ?: 0) > 1) {
        state.measures.map { DropDownItem(text = it.plural) }
    } else {
        state.measures.map { DropDownItem(text = it.name) }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = PADDING_MEDIUM)
            .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_EXTRA_SMALL),
    ) {
        CustomTextField(
            value = state.containerValue,
            onValueChange = { newContainer ->
                onEvent(AddProductEvent.OnContainerValueChanges(newContainer))
            },
            placeholder = "0",
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
            modifier = Modifier
                .padding(horizontal = PADDING_SMALL)
                .fillMaxWidth(0.25f)
        )

        DropdownSelectable(
            currentSelected = state.containerType.ifEmpty { "Selecciona envase" },
            dropdownItems = containerDropdownItems,
            onItemClick = {
                onEvent(AddProductEvent.OnContainerTypeChanges(it.text))
            },
            modifier = Modifier
                .padding(horizontal = PADDING_EXTRA_SMALL)
                .fillMaxWidth()
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = PADDING_MEDIUM, vertical = PADDING_EXTRA_SMALL),
    ) {
        CustomTextField(
            value = state.measureValue,
            onValueChange = { newMeasure ->
                onEvent(AddProductEvent.OnMeasuresValueChanges(newMeasure))
            },
            placeholder = "0",
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Next,
            modifier = Modifier
                .padding(horizontal = PADDING_SMALL)
                .fillMaxWidth(0.25f)
        )

        DropdownSelectable(
            currentSelected = state.measureType.ifEmpty { "Selecciona unidad" },
            dropdownItems = measureDropdownItems,
            onItemClick = {
                onEvent(AddProductEvent.OnMeasuresTypeChanges(it.text))
            },
            modifier = Modifier
                .padding(horizontal = PADDING_EXTRA_SMALL)
                .fillMaxWidth()
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
