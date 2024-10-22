package com.reguerta.presentation.screen.products.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.usecase.container.GetFilteredContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.products.AddProductUseCase
import com.reguerta.presentation.checkAllStringAreNotEmpty
import com.reguerta.presentation.getContainerPluralForm
import com.reguerta.presentation.getContainerSingularForm
import com.reguerta.presentation.getMeasurePluralForm
import com.reguerta.presentation.getMeasureSingularForm
import com.reguerta.presentation.resizeAndCropImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_product
 * Created By Manuel Lopera on 1/3/24 at 17:02
 * All rights reserved 2024
 */

@HiltViewModel
class AddProductViewModel @Inject constructor(
    getAllMeasuresUseCase: GetAllMeasuresUseCase,
    getFilteredContainersUseCase: GetFilteredContainersUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<AddProductState> = MutableStateFlow(AddProductState())
    val state: StateFlow<AddProductState> = _state.asStateFlow()

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
                    getFilteredContainersUseCase().collect { containerList ->
                        _state.update {
                            it.copy(
                                containers = containerList
                            )
                        }
                    }
                }
            ).awaitAll()
        }
    }

    fun onEvent(event: AddProductEvent) {
        viewModelScope.launch {
            when (event) {
                AddProductEvent.GoOut -> {
                    _state.update {
                        it.copy(
                            goOut = true
                        )
                    }
                }

                is AddProductEvent.OnAvailableChanges -> {
                    _state.update {
                        it.copy(
                            isAvailable = event.newValue
                        )
                    }
                }

                is AddProductEvent.OnDescriptionChanged -> {
                    _state.update {
                        it.copy(
                            description = event.newValue
                        )
                    }
                }

                is AddProductEvent.OnImageSelected -> {
                    _state.update {
                        it.copy(
                            bitmap = event.bitmap
                        )
                    }
                }

                is AddProductEvent.OnNameChanged -> {
                    _state.update {
                        it.copy(
                            name = event.newValue
                        )
                    }
                }

                is AddProductEvent.OnPriceChanged -> {
                    _state.update {
                        it.copy(
                            price = event.newValue
                        )
                    }
                }

                is AddProductEvent.OnStockChanged -> {
                    _state.update {
                        it.copy(
                            stock = event.newValue
                        )
                    }
                }

                AddProductEvent.AddProduct -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        with(_state.value) {
                            val imageByteArray = bitmap?.let { resizeAndCropImage(it) }
                            val productToAdd = CommonProduct(
                                container = containerType,
                                description = description,
                                name = name,
                                price = price.replace(",", ".").toFloat(),
                                available = isAvailable,
                                stock = stock,
                                quantityContainer = containerValue.toInt(),
                                quantityWeight = measureValue.toInt(),
                                unity = measureType
                            )

                            addProductUseCase(
                                productToAdd,
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

                is AddProductEvent.OnContainerTypeChanges -> {
                    _state.update {
                        it.copy(
                            containerType = event.newContainerType
                        )
                    }
                }

                is AddProductEvent.OnContainerValueChanges -> {
                    _state.update {
                        val newContainerType = if ((event.newContainerValue.toIntOrNull() ?: 0) > 1) {
                            getContainerPluralForm(it.containerType, it.containers)
                        } else {
                            getContainerSingularForm(it.containerType, it.containers)
                        }
                        it.copy(
                            containerValue = event.newContainerValue,
                            containerType = newContainerType
                        )
                    }
                }

                is AddProductEvent.OnMeasuresTypeChanges -> {
                    _state.update {
                        it.copy(
                            measureType = event.newMeasureType
                        )
                    }
                }

                is AddProductEvent.OnMeasuresValueChanges -> {
                    _state.update {
                        val newMeasureType = if ((event.newMeasureValue.toIntOrNull() ?: 0) > 1) {
                            getMeasurePluralForm(it.measureType, it.measures)
                        } else {
                            getMeasureSingularForm(it.measureType, it.measures)
                        }
                        it.copy(
                            measureValue = event.newMeasureValue,
                            measureType = newMeasureType
                        )
                    }
                }
            }

            if (event !is AddProductEvent.GoOut && event !is AddProductEvent.AddProduct) {
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