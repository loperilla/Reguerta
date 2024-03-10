package com.reguerta.presentation.screen.products.edit

import android.graphics.Bitmap

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_product
 * Created By Manuel Lopera on 3/3/24 at 15:03
 * All rights reserved 2024
 */
sealed class EditProductEvent {
    data object GoOut : EditProductEvent()
    data class OnImageSelected(val bitmap: Bitmap) : EditProductEvent()
    data class OnAvailableChanges(val newValue: Boolean) : EditProductEvent()
    data class OnNameChanged(val newValue: String) : EditProductEvent()
    data class OnDescriptionChanged(val newValue: String) : EditProductEvent()
    data class OnPriceChanged(val newValue: String) : EditProductEvent()
    data class OnStockChanged(val newValue: Int) : EditProductEvent()
    data class OnMeasuresValueChanges(val newMeasureValue: String) : EditProductEvent()
    data class OnMeasuresTypeChanges(val newMeasureType: String) : EditProductEvent()
    data class OnContainerValueChanges(val newContainerValue: String) : EditProductEvent()
    data class OnContainerTypeChanges(val newContainerType: String) : EditProductEvent()

    data object SaveProduct : EditProductEvent()
}