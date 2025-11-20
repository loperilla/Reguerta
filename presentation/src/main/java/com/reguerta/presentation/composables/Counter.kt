package com.reguerta.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.reguerta.presentation.ui.Dimens

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 2/3/24 at 10:53
 * All rights reserved 2024
 */

@Composable
fun ReguertaCounter(
    value: Int,
    onMinusButtonClicked: () -> Unit,
    onPlusButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .wrapContentSize()
            .heightIn(min = Dimens.Components.Counter.minHeight)
            .background(
                color = Dimens.Components.Counter.containerColor,
                shape = RoundedCornerShape(Dimens.Components.Counter.cornerRadius)
            )
            .padding(
                horizontal = Dimens.Components.Counter.horizontalPadding,
                vertical = Dimens.Components.Counter.verticalPadding
            )
    ) {
        IconButton(
            onClick = onMinusButtonClicked,
            enabled = value > 0
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "counter remove",
                tint = if (value > 0) Dimens.Components.Counter.contentColor else Dimens.Components.Counter.disabledContentColor,
                modifier = Modifier.size(Dimens.Components.Counter.iconSize)
            )
        }
        IconButton(
            onClick = onPlusButtonClicked
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "counter add",
                tint = Dimens.Components.Counter.contentColor,
                modifier = Modifier.size(Dimens.Components.Counter.iconSize)
            )
        }
    }
}
