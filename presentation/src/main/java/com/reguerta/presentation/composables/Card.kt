package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.reguerta.presentation.ui.PrimaryColor

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
    containerColor: Color = PrimaryColor.copy(0.15f),
    contentColor: Color = Color.Unspecified,
) {
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
    Card(
        modifier = modifier,
        colors = colors,
        content = content
    )
}