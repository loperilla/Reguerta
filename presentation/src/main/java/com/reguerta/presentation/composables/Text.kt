package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.reguerta.presentation.ui.Orange
import com.reguerta.presentation.ui.PADDING_MEDIUM
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.TEXT_SIZE_LARGE
import com.reguerta.presentation.ui.TEXT_SIZE_MEDIUM
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL
import com.reguerta.presentation.ui.TEXT_TOP_BAR
import com.reguerta.presentation.ui.Text
import com.reguerta.presentation.ui.cabinsketchFontFamily

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:07
 * All rights reserved 2024
 */

@Composable
fun TextRegular(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_SMALL,
    textColor: Color = Color.Unspecified
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = FontWeight.Normal,
        fontFamily = cabinsketchFontFamily
    )
}

@Composable
fun TextBody(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_MEDIUM,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text,
        modifier,
        color = textColor,
        fontSize = textSize,
        fontWeight = fontWeight,
        fontFamily = cabinsketchFontFamily,
        textAlign = textAlignment
    )
}

@Composable
fun TextBody(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_MEDIUM,
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
    stockCount: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_MEDIUM
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> Color.Red
        in 1..10 -> Orange
        else -> Text
    }
    TextBody(
        text = "Stock: $stockCount",
        modifier,
        textSize,
        textColor = colorToDraw,
    )
}

@Composable
fun AmountText(
    amount: Float,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextTitle(
            text = "Total: ",
            textSize = TEXT_SIZE_LARGE,
            textColor = PrimaryColor
        )
        TextBody(
            text = String.format("%.2f", amount) + "€",
            textSize = TEXT_TOP_BAR,
            textColor = Text
        )
    }
}

@Composable
fun TextTitle(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_LARGE,
    textColor: Color = Color.Unspecified,
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

@Composable
fun TextTitle(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TEXT_SIZE_LARGE,
    textColor: Color = Color.Unspecified,
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
            verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM), modifier = Modifier.fillMaxSize()
        ) {
            TextRegular("Manuel Lopera")
            TextBody("Manuel Lopera")
            TextTitle("Manuel Lopera")
            AmountText(15f)
            StockText(0)
            StockText(4)
            StockText(34)
        }
    }
}