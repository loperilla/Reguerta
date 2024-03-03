package com.reguerta.presentation.screen.edit_product

import dagger.assisted.AssistedFactory

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_product
 * Created By Manuel Lopera on 3/3/24 at 15:04
 * All rights reserved 2024
 */
@AssistedFactory
interface EditProductViewModelFactory {
    fun create(productId: String): EditProductViewModel
}