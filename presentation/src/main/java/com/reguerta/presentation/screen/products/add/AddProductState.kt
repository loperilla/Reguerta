package com.reguerta.presentation.screen.products.add

import android.graphics.Bitmap
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_product
 * Created By Manuel Lopera on 1/3/24 at 17:03
 * All rights reserved 2024
 */

data class AddProductState(
    val goOut: Boolean = false,
    val bitmap: Bitmap? = null,
    val name: String = "",
    val description: String = "",
    val isAvailable: Boolean = true,
    val price: String = "",
    val stock: Int = 0,
    val containerValue: String = "",
    val containerType: String = "",
    val measureValue: String = "",
    val measureType: String = "",
    val isButtonEnabled: Boolean = false,
    val measures: List<Measure> = emptyList(),
    val containers: List<Container> = emptyList()
)
