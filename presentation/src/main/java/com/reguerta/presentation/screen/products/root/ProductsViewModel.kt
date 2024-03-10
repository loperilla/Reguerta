package com.reguerta.presentation.screen.products.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.products.DeleteProductUseCase
import com.reguerta.domain.usecase.products.GetAllProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.products
 * Created By Manuel Lopera on 25/2/24 at 11:37
 * All rights reserved 2024
 */
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<ProductsState> = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsUseCase().collect {
                _state.value = state.value.copy(
                    products = it,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: ProductsEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is ProductsEvent.ShowAreYouSureDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSureDialog = true,
                            selectedProductToDelete = event.idToDelete
                        )
                    }
                }

                ProductsEvent.GoOut -> {
                    _state.update {
                        it.copy(
                            goOut = true
                        )
                    }
                }

                ProductsEvent.ConfirmDeleteProduct -> {
                    deleteProductUseCase(state.value.selectedProductToDelete).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    showAreYouSureDialog = false,
                                    selectedProductToDelete = ""
                                )
                            }
                        },
                        onFailure = {
                            _state.update {
                                it.copy(
                                    showAreYouSureDialog = false,
                                    selectedProductToDelete = ""
                                )
                            }
                        }
                    )
                }

                ProductsEvent.HideAreYouSureDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSureDialog = false,
                            selectedProductToDelete = ""
                        )
                    }
                }
            }
        }
    }
}