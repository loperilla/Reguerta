package com.reguerta.presentation.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.reguerta.presentation.ui.cabinsketchFontFamily
import com.reguerta.presentation.ui.monserratFontFamily

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:07
 * All rights reserved 2024
 */

@Composable
fun TextRegular(
    text: String,
    textSize: TextUnit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Medium,
        fontFamily = monserratFontFamily
    )
}

@Composable
fun TextTitle(
    text: String,
    textSize: TextUnit,
    textColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Bold,
        fontFamily = cabinsketchFontFamily
    )
}