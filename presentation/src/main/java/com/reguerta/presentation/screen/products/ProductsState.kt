package com.reguerta.presentation.screen.products

import com.reguerta.domain.model.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.products
 * Created By Manuel Lopera on 25/2/24 at 11:41
 * All rights reserved 2024
 */
data class ProductsState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val goOut: Boolean = false,
    val showAreYouSureDialog: Boolean = false,
    val selectedProductToDelete: String = ""
)
