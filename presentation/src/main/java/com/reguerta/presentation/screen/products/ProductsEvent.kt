package com.reguerta.presentation.screen.products

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.products
 * Created By Manuel Lopera on 25/2/24 at 11:42
 * All rights reserved 2024
 */
sealed class ProductsEvent {
    data class DeleteProduct(val idToDelete: String) : ProductsEvent()
    data object GoOut : ProductsEvent()
}