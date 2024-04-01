package com.reguerta.presentation.composables.products

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.products
 * Created By Manuel Lopera on 1/4/24 at 20:02
 * All rights reserved 2024
 */

@Composable
fun ReguertaDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) = Canvas(modifier.wrapContentHeight().width(thickness)) {
    drawLine(
        color = color,
        strokeWidth = thickness.toPx(),
        start = Offset(thickness.toPx() / 2, 0f),
        end = Offset(thickness.toPx() / 2, size.height),
    )
}