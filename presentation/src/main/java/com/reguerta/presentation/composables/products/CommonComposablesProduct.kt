package com.reguerta.presentation.composables.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.mapper.containerUnity
import com.reguerta.presentation.composables.TextBody
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM
import com.reguerta.presentation.ui.Text

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
        TextBody(
            text = product.name,
            textSize = TEXT_SIZE_LARGE,
            textColor = Text,
            modifier = Modifier
                .padding(PADDING_EXTRA_SMALL),
            textAlignment = TextAlign.Start
        )
        TextBody(
            text = product.containerUnity(),
            textSize = TEXT_SIZE_MEDIUM,
            textColor = Text,
            modifier = Modifier
                .padding(PADDING_EXTRA_SMALL),
            textAlignment = TextAlign.Start
        )
    }
}