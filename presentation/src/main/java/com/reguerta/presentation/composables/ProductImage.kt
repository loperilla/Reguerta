package com.reguerta.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.presentation.R
import com.reguerta.presentation.ui.Dimens

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
    imageSize: Dp = Dimens.Size.dp72
) {
    if (product.imageUrl.isEmpty()) {
        Image(
            painter = painterResource(id = R.mipmap.product_no_available),
            contentDescription = product.name,
            modifier = modifier
                .padding(Dimens.Spacing.sm)
                .size(imageSize)
                .clip(RoundedCornerShape(Dimens.Radius.md))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(Dimens.Radius.md)
                )
                .border(
                    width = Dimens.Border.regular,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(Dimens.Radius.md)
                ),
            contentScale = ContentScale.Crop
        )
    } else {
        ImageUrl(
            imageUrl = product.imageUrl,
            name = product.name,
            modifier = modifier
                .padding(Dimens.Spacing.sm)
                .size(imageSize)
                .clip(RoundedCornerShape(Dimens.Radius.md))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(Dimens.Radius.md)
                )
                .border(
                    width = Dimens.Border.regular,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(Dimens.Radius.md)
                )
        )
    }
}