package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PrimaryColor

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 11/2/24 at 10:51
 * All rights reserved 2024
 */

@Composable
fun ReguertaCheckBox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    val cbColors = CheckboxDefaults.colors(
        checkedColor = PrimaryColor,
        checkmarkColor = Color.White,
        uncheckedColor = Color.Gray
    )
    Checkbox(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = isEnabled,
        colors = cbColors
    )
}

@Preview
@Composable
fun CheckboxPreview() {
    Screen {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL),
            modifier = Modifier.fillMaxSize()
        ) {
            ReguertaCheckBox(
                isChecked = true,
                onCheckedChange = {},
                modifier = Modifier
            )
            ReguertaCheckBox(
                isChecked = false,
                onCheckedChange = {},
                modifier = Modifier
            )

        }

    }
}