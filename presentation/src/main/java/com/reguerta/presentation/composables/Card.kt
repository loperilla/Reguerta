package com.reguerta.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reguerta.presentation.ui.Dimens

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 3/3/24 at 13:16
 * All rights reserved 2024
 */

@Composable
fun ReguertaCard(
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    cornerRadius: Dp = Dimens.Radius.md,
    elevation: Dp = 0.dp,
    border: BorderStroke? = null,
    kind: Dimens.Components.Card.Kind = Dimens.Components.Card.Kind.Filled
) {
    val finalElevation = if (elevation == 0.dp) Dimens.Components.Card.elevation(kind) else elevation
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    Card(
        modifier = modifier,
        colors = colors,
        elevation = CardDefaults.cardElevation(defaultElevation = finalElevation),
        shape = RoundedCornerShape(cornerRadius),
        border = border ?: Dimens.Components.Card.border(kind),
        content = content
    )
}