package com.reguerta.presentation.composables

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
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaButtonPreview() {
    Screen {
        Column {
            ReguertaButton(
                textButton = "Button",
                onClick = {}
            )
        }
    }
}
