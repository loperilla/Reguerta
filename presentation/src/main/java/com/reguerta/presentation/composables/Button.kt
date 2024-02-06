package com.reguerta.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.SecondaryBackground
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:57
 * All rights reserved 2024
 */

@Composable
fun ReguertaButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabledButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            disabledContainerColor = Color.Gray.copy(alpha = 0.15f)
        )
    ) {
        TextRegular(
            text = textButton,
            textSize = 14.sp,
            textColor = Color.White,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
fun InverseReguertaButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, PrimaryColor),
        enabled = enabledButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryBackground
        )
    ) {
        TextBody(
            text = textButton,
            textSize = 16.sp,
            textColor = Text,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaButtonPreview() {
    Screen {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReguertaButton(
                textButton = "Button",
                onClick = {}
            )
            ReguertaButton(
                textButton = "Button",
                onClick = {},
                enabledButton = false
            )
            InverseReguertaButton(
                textButton = "Button",
                onClick = {}
            )
            InverseReguertaButton(
                textButton = "Button",
                onClick = {},
                enabledButton = false
            )
        }
    }
}
