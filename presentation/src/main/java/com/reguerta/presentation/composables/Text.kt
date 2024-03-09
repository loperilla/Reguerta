package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.ui.Text
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
    text: String, textSize: TextUnit, modifier: Modifier = Modifier, textColor: Color = Color.Unspecified
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Normal,
        fontFamily = monserratFontFamily
    )
}

@Composable
fun TextBody(
    text: String,
    textSize: TextUnit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Normal,
        fontFamily = cabinsketchFontFamily,
        textAlign = textAlignment
    )
}

@Composable
fun StockText(
    stockCount: Int, textSize: TextUnit, modifier: Modifier = Modifier
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> Color.Red
        in 1..10 -> Color(0xFFffa500) // Orange
        else -> Text
    }
    TextBody(
        text = "Stock: $stockCount",
        textSize,
        modifier,
        textColor = colorToDraw,
    )
}

@Composable
fun TextTitle(
    text: String, textSize: TextUnit, textColor: Color = Color.Unspecified, modifier: Modifier = Modifier,
    textAlignment: TextAlign? = null
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Bold,
        fontFamily = cabinsketchFontFamily,
        textAlign = textAlignment
    )
}

@Preview
@Composable
fun TextPreviews() {
    Screen {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()
        ) {
            TextRegular("Manuel Lopera", textSize = 14.sp)
            TextBody("Manuel Lopera", textSize = 14.sp)
            TextTitle("Manuel Lopera", textSize = 14.sp)
            StockText(0, textSize = 14.sp)
            StockText(4, textSize = 14.sp)
            StockText(34, textSize = 14.sp)
        }
    }
}