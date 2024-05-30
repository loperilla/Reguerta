package com.reguerta.presentation.screen.products.edit

import android.graphics.Bitmap
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_product
 * Created By Manuel Lopera on 3/3/24 at 15:02
 * All rights reserved 2024
 */

data class EditProductState(
    val goOut: Boolean = false,
    val bitmap: Bitmap? = null,
    val name: String = "",
    val description: String = "",
    val isAvailable: Boolean = true,
    val imageUrl: String = "",
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
