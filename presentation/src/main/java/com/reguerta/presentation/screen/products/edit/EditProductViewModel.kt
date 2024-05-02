package com.reguerta.presentation.screen.products.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.usecase.container.GetAllContainerUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.products.EditProductUseCase
import com.reguerta.domain.usecase.products.GetProductByIdUseCase
import com.reguerta.presentation.checkAllStringAreNotEmpty
import com.reguerta.presentation.resizeAndCropImage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_product
 * Created By Manuel Lopera on 3/3/24 at 15:03
 * All rights reserved 2024
 */
@HiltViewModel(assistedFactory = EditProductViewModelFactory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted private val productId: String,
    getAllMeasuresUseCase: GetAllMeasuresUseCase,
    getAllContainerUseCase: GetAllContainerUseCase,
    getProductByIdUseCase: GetProductByIdUseCase,
    private val editProductUseCase: EditProductUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<EditProductState> = MutableStateFlow(EditProductState())
    val state: StateFlow<EditProductState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listOf(
                async {
                    getAllMeasuresUseCase().collect { measureList ->
                        _state.update {
                            it.copy(
                                measures = measureList
                            )
                        }
                    }
                },
                async {
                    getAllContainerUseCase().collect { containerList ->
                        _state.update {
                            it.copy(
                                containers = containerList
                            )
                        }
                    }
                },
                async {
                    getProductByIdUseCase(productId).fold(
                        onSuccess = { product ->
                            Timber.d("Product: $product")
                            _state.update {
                                it.copy(
                                    name = product.name,
                                    description = product.description,
                                    isAvailable = product.available,
                                    price = product.price.toString(),
                                    stock = product.stock,
                                    imageUrl = product.imageUrl,
                                    containerValue = product.quantityContainer.toString(),
                                    containerType = product.container,
                                    measureValue = product.quantityWeight.toString(),
                                    measureType = product.unity,
                                )
                            }
                        },
                        onFailure = {
                            it.printStackTrace()
                        }
                    )
                }
            ).awaitAll()
        }
    }

    fun onEvent(newEvent: EditProductEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (newEvent) {
                EditProductEvent.GoOut -> {
                    _state.update {
                        it.copy(
                            goOut = true
                        )
                    }
                }

                EditProductEvent.SaveProduct -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        with(state.value) {
                            val imageByteArray = if (bitmap != null) {
                                async { resizeAndCropImage(bitmap) }.await()
                            } else {
                                // Aquí deberías tener algún mecanismo para convertir la URL existente en byteArray.
                                // Esto puede requerir descargar la imagen de nuevo o mejor aún, tener un byteArray guardado.
                                null
                            }

                            val productToSave = CommonProduct(
                                container = containerType,
                                description = description,
                                name = name,
                                price = price.replace(",", ".").toFloat(),
                                available = isAvailable,
                                stock = stock,
                                quantityContainer = containerValue.toInt(),
                                quantityWeight = measureValue.toInt(),
                                unity = measureType,
                                imageUrl = imageUrl
                            )

                            editProductUseCase(
                                id = productId,
                                productToSave,
                                imageByteArray
                            ).fold(
                                onSuccess = {
                                    _state.update {
                                        it.copy(
                                            goOut = true
                                        )
                                    }
                                },
                                onFailure = {
                                    it.printStackTrace()
                                }
                            )
                        }
                    }
                }

                is EditProductEvent.OnAvailableChanges -> {
                    _state.update {
                        it.copy(
                            isAvailable = newEvent.newValue
                        )
                    }
                }

                is EditProductEvent.OnContainerTypeChanges -> {
                    _state.update {
                        it.copy(
                            containerType = newEvent.newContainerType
                        )
                    }
                }

                is EditProductEvent.OnContainerValueChanges -> {
                    _state.update {
                        it.copy(
                            containerValue = newEvent.newContainerValue
                        )
                    }
                }

                is EditProductEvent.OnDescriptionChanged -> {
                    _state.update {
                        it.copy(
                            description = newEvent.newValue
                        )
                    }
                }

                is EditProductEvent.OnImageSelected -> {
                    _state.update {
                        it.copy(
                            bitmap = newEvent.bitmap
                        )
                    }
                }

                is EditProductEvent.OnMeasuresTypeChanges -> {
                    _state.update {
                        it.copy(
                            measureType = newEvent.newMeasureType
                        )
                    }
                }

                is EditProductEvent.OnMeasuresValueChanges -> {
                    _state.update {
                        it.copy(
                            measureValue = newEvent.newMeasureValue
                        )
                    }
                }

                is EditProductEvent.OnNameChanged -> {
                    _state.update {
                        it.copy(
                            name = newEvent.newValue
                        )
                    }
                }

                is EditProductEvent.OnPriceChanged -> {
                    _state.update {
                        it.copy(
                            price = newEvent.newValue
                        )
                    }
                }

                is EditProductEvent.OnStockChanged -> {
                    _state.update {
                        it.copy(
                            stock = newEvent.newValue
                        )
                    }
                }
            }
            if (newEvent !is EditProductEvent.GoOut && newEvent !is EditProductEvent.SaveProduct) {
                _state.update {
                    it.copy(
                        isButtonEnabled = checkIfButtonIsEnabled()
                    )
                }
            }
        }
    }

    private fun checkIfButtonIsEnabled(): Boolean {
        with(_state.value) {
            return checkAllStringAreNotEmpty(
                name,
                description,
                price,
                measureType,
                containerType,
                measureValue,
                containerValue
            )
        }
    }
}