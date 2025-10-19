package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.mapper.containerUnity
import com.reguerta.presentation.ui.Dimens

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.products
 * Created By Manuel Lopera on 29/3/24 at 14:18
 * All rights reserved 2024
 */

@Composable
fun ProductNameUnityContainer(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TextTitle(
            text = product.name,
            textSize = MaterialTheme.typography.titleMedium.fontSize,
            textColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(Dimens.Spacing.xs),
            textAlignment = TextAlign.Center
        )
        TextBody(
            text = product.containerUnity(),
            textSize = MaterialTheme.typography.bodySmall.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(Dimens.Spacing.xs),
            textAlignment = TextAlign.Center
        )
    }
}

@Composable
fun ProductNameUnityContainerInMyOrder(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(start = Dimens.Spacing.xs)
    ) {
        TextBody(
            text = product.name,
            textSize = MaterialTheme.typography.bodyMedium.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                top = Dimens.Spacing.xs)
        )
        TextBody(
            text = product.containerUnity(),
            textSize = MaterialTheme.typography.bodySmall.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = Dimens.Spacing.xxs),
            textAlignment = TextAlign.Start
        )
    }
}