package com.reguerta.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.reguerta.presentation.ui.CORNER_SIZE_8
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.Text

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
            .padding(horizontal = PADDING_SMALL)
            .wrapContentSize()
            .background(
                Color.LightGray,
                shape = RoundedCornerShape(CORNER_SIZE_8)
            )
    ) {
        IconButton(
            onClick = onMinusButtonClicked,
            enabled = value > 0
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "counter remove",
                tint = Text
            )
        }
        IconButton(
            onClick = onPlusButtonClicked
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "counter add",
                tint = Text
            )
        }
    }
}

@Preview
@Composable
fun ReguertaCounterPreview() {
    Screen {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ReguertaCounter(
                0,
                onMinusButtonClicked = {},
                onPlusButtonClicked = {},
            )
        }
    }
}