package com.reguerta.presentation.composables

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.reguerta.presentation.R
import com.reguerta.presentation.ui.Dimens

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
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    cornerSize: Dp = Dimens.Radius.md,
    clip: Boolean = true,
    crossfadeMillis: Int = 250
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(imageUrl.takeIf { it.isNotBlank() })
            .crossfade(crossfadeMillis)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        placeholder = painterResource(R.mipmap.product_no_available),
        error = painterResource(R.mipmap.product_no_available),
        fallback = painterResource(R.mipmap.product_no_available),
        contentDescription = contentDescription ?: "${name}_image",
        contentScale = contentScale,
        modifier = if (clip) {
            modifier.clip(RoundedCornerShape(CornerSize(cornerSize)))
        } else {
            modifier
        }
    )
}