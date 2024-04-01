package com.reguerta.presentation.composables.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.presentation.R
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.SIZE_96

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 10/3/24 at 12:32
 * All rights reserved 2024
 */
@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier,
    imageSize: Dp = SIZE_96
) {
    if (product.imageUrl.isEmpty()) {
        Image(
            painter = painterResource(id = R.mipmap.product_no_available),
            contentDescription = product.name,
            modifier = modifier
                .padding(PADDING_SMALL)
                .size(imageSize)
        )
    } else {
        ImageUrl(
            imageUrl = product.imageUrl,
            name = product.name,
            modifier = modifier
                .padding(PADDING_SMALL)
                .size(imageSize)
        )
    }
}