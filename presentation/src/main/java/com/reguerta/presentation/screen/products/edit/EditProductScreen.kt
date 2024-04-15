package com.reguerta.presentation.screen.products.edit

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
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.reguerta.presentation.R
import com.reguerta.presentation.checkRationalPermission
import com.reguerta.presentation.checkStoragePermission
import com.reguerta.presentation.composables.DropDownItem
import com.reguerta.presentation.composables.DropdownSelectable
import com.reguerta.presentation.composables.ReguertaButton
import com.reguerta.presentation.composables.ReguertaCheckBox
import com.reguerta.presentation.composables.ReguertaCounter
import com.reguerta.presentation.composables.ReguertaTopBar
import com.reguerta.presentation.composables.Screen
import com.reguerta.presentation.composables.SecondaryTextReguertaInput
import com.reguerta.presentation.composables.StockText
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.composables.TextReguertaInput
import com.reguerta.presentation.getStoragePermissionBySdk
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.SIZE_96
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.Text
import com.reguerta.presentation.uriToBitmap
import kotlinx.coroutines.launch

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_product
 * Created By Manuel Lopera on 3/3/24 at 15:02
 * All rights reserved 2024
 */

@Composable
fun editProductScreen(
    id: String,
    navigateTo: () -> Unit
) {
    val viewModel = hiltViewModel<EditProductViewModel, EditProductViewModelFactory> { factory ->
        factory.create(id)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.goOut) {
        navigateTo()
        return
    }
    Screen {
        EditProductScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun EditProductScreen(
    state: EditProductState,
    onEvent: (EditProductEvent) -> Unit
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
                topBarText = "Editando producto",
                navActionClick = { onEvent(EditProductEvent.GoOut) }
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
                    onEvent(EditProductEvent.OnNameChanged(newName))
                },
                labelText = "Nombre del producto",
                placeholderText = "Pulsa para escribir",
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .fillMaxWidth()
            )

            TextReguertaInput(
                text = state.description,
                onTextChange = { newDescription ->
                    onEvent(EditProductEvent.OnDescriptionChanged(newDescription))
                },
                imeAction = ImeAction.Next,
                labelText = "Descripción del producto",
                placeholderText = "Pulsa para escribir",
                modifier = Modifier
                    .padding(PADDING_SMALL)
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
                        EditProductEvent.OnPriceChanged(newPrice)
                    )
                },
                imeAction = ImeAction.Next,
                labelText = "Precio",
                placeholderText = "Pulsa para escribir",
                keyboardType = KeyboardType.Number,
                suffixValue = "€",
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .fillMaxWidth()
            )

            ReguertaButton(
                textButton = "Actualizar producto",
                enabledButton = state.isButtonEnabled,
                onClick = { onEvent(EditProductEvent.SaveProduct) },
                modifier = Modifier
                    .padding(PADDING_SMALL)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HeaderAddProductForm(
    state: EditProductState,
    onEvent: (EditProductEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var photoUri: Uri? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = state.imageUrl) {
        state.imageUrl.let { imageUrl ->
            photoUri = Uri.parse(imageUrl)
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        photoUri = uri ?: Uri.parse(state.imageUrl)
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
                ContextCompat.startActivity(
                    context,
                    intent,
                    null
                )
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
                    EditProductEvent.OnImageSelected(
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
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PADDING_EXTRA_SMALL, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextBody(
                    text = "Disponible",
                    textSize = TEXT_SIZE_LARGE,
                    textColor = Text
                )
                ReguertaCheckBox(
                    isChecked = state.isAvailable,
                    onCheckedChange = { newValue ->
                        onEvent(EditProductEvent.OnAvailableChanges(newValue))
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
                        onEvent(EditProductEvent.OnStockChanged(state.stock.minus(1)))
                    },
                    onPlusButtonClicked = {
                        onEvent(EditProductEvent.OnStockChanged(state.stock.plus(1)))
                    }
                )
            }
        }
    }
}

@Composable
private fun UnityAndContainer(
    state: EditProductState,
    onEvent: (EditProductEvent) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(PADDING_SMALL),
    ) {
        SecondaryTextReguertaInput(
            text = state.containerValue,
            onTextChange = { newContainer ->
                onEvent(EditProductEvent.OnContainerValueChanges(newContainer))
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.NumberPassword,
            placeholderText = "0",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(PADDING_SMALL)
        )

        DropdownSelectable(
            currentSelected = state.containerType.ifEmpty {
                "Selecciona envase"
            },
            dropdownItems = state.containers.map {
                DropDownItem(text = it.name)
            },
            onItemClick = {
                onEvent(EditProductEvent.OnContainerTypeChanges(it.text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(PADDING_SMALL)
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(PADDING_SMALL),
    ) {
        SecondaryTextReguertaInput(
            text = state.measureValue,
            onTextChange = { newMeasure ->
                onEvent(EditProductEvent.OnMeasuresValueChanges(newMeasure))
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.NumberPassword,
            placeholderText = "0",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(PADDING_SMALL)
        )

        DropdownSelectable(
            currentSelected = state.measureType.ifEmpty {
                "Selecciona unidad"
            },
            dropdownItems = state.measures.map {
                DropDownItem(text = it.name)
            },
            onItemClick = {
                onEvent(EditProductEvent.OnMeasuresTypeChanges(it.text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(PADDING_SMALL)
        )
    }
}

@Preview
@Composable
fun EditProductScreenPreview() {
    Screen {
        EditProductScreen(
            state = EditProductState(),
            onEvent = {}
        )
    }
}