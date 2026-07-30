package com.reguerta.presentation.composables

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.reguerta.presentation.ui.Dimens

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.products
 * Created By Manuel Lopera on 1/4/24 at 20:02
 * All rights reserved 2024
 */

@Composable
fun ReguertaDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dimens.Components.Divider.thickness,
    color: Color = Dimens.Components.Divider.color,
    vertical: Boolean = true
) {
    if (vertical) {
        VerticalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color
        )
    } else {
        HorizontalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color
        )
    }
}

@Composable
fun ReguertaHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dimens.Components.Divider.thickness,
    color: Color = Dimens.Components.Divider.color,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
fun ReguertaVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = Dimens.Components.Divider.thickness,
    color: Color = Dimens.Components.Divider.color,
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}