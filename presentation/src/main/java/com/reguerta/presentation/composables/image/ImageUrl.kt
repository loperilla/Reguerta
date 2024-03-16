package com.reguerta.presentation.composables.image

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.reguerta.presentation.R

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 1/3/24 at 14:56
 * All rights reserved 2024
 */

@Composable
fun ImageUrl(
    imageUrl: String,
    name: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .memoryCacheKey("${name}_image")
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .scale(Scale.FILL)
            .build(),
        placeholder = painterResource(R.mipmap.product_no_available),
        contentDescription = "${name}_image",
        modifier = modifier
            .clip(RoundedCornerShape(CornerSize(12.dp)))
    )
}