package com.reguerta.presentation.screen.add_product

import android.net.Uri

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_product
 * Created By Manuel Lopera on 1/3/24 at 17:03
 * All rights reserved 2024
 */
sealed class AddProductEvent {
    data object GoOut : AddProductEvent()
    data class OnImageSelected(val uri: Uri?) : AddProductEvent()
    data class OnAvailableChanges(val newValue: Boolean) : AddProductEvent()
    data class OnNameChanged(val newValue: String) : AddProductEvent()
    data class OnDescriptionChanged(val newValue: String) : AddProductEvent()
    data class OnPriceChanged(val newValue: String) : AddProductEvent()
    data class OnStockChanged(val newValue: Int) : AddProductEvent()
    data class OnMeasuresValueChanges(val newMeasureValue: String) : AddProductEvent()
    data class OnMeasuresTypeChanges(val newMeasureType: String) : AddProductEvent()
    data class OnContainerValueChanges(val newContainerValue: String) : AddProductEvent()
    data class OnContainerTypeChanges(val newContainerType: String) : AddProductEvent()

    data object AddProduct : AddProductEvent()
}