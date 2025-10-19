package com.reguerta.presentation.composables

import java.util.Locale

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.reguerta.presentation.ui.Dimens
import com.reguerta.presentation.ui.CabinSketchFontFamily
import com.reguerta.presentation.ui.LowStock

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
    textSize: TextUnit = MaterialTheme.typography.labelMedium.fontSize,
    textColor: Color = Color.Unspecified,
    style: TextStyle? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        style = (style ?: MaterialTheme.typography.labelMedium).copy(
            fontSize = textSize,
            fontWeight = FontWeight.Normal,
            fontFamily = CabinSketchFontFamily
        )
    )
}

@Composable
fun TextBody(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    style: TextStyle? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = (style ?: MaterialTheme.typography.bodyLarge).copy(
            fontSize = textSize,
            fontWeight = fontWeight,
            fontFamily = CabinSketchFontFamily
        )
    )
}

@Composable
fun TextBody(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = (style ?: MaterialTheme.typography.bodyLarge).copy(
            fontSize = textSize,
            fontWeight = FontWeight.Normal,
            fontFamily = CabinSketchFontFamily
        )
    )
}

@Composable
fun StockProductText(
    stockCount: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> MaterialTheme.colorScheme.error
        in 1..10 -> LowStock
        else -> MaterialTheme.colorScheme.onSurface
    }
    TextBody(
        text = "Stock: $stockCount",
        modifier,
        textSize,
        textColor = colorToDraw,
    )
}

@Composable
fun StockOrderText(
    stockCount: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> MaterialTheme.colorScheme.error
        in 1..10 -> LowStock
        in 11..20 -> MaterialTheme.colorScheme.onSurface
        else -> Color.Transparent
    }
    TextBody(
        text = "Quedan: $stockCount uds.",
        modifier,
        textSize,
        textColor = colorToDraw,
    )
}

@Composable
fun AmountText(
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextTitle(
            text = "Total: ",
            textSize = MaterialTheme.typography.bodyLarge.fontSize,
            textColor = MaterialTheme.colorScheme.primary
        )
        TextBody(
            text = String.format(Locale.getDefault(), "%.2f €", amount),
            textSize = MaterialTheme.typography.titleLarge.fontSize,
            textColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HeaderSectionText(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textAlignment: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
    padding: Dp = Dimens.Spacing.sm,
    style: TextStyle? = null
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(Dimens.Radius.md))
            .fillMaxWidth()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            textAlign = textAlignment,
            style = (style ?: MaterialTheme.typography.titleLarge).copy(
                fontSize = textSize,
                fontWeight = fontWeight,
                fontFamily = CabinSketchFontFamily
            )
        )
    }
}

@Composable
fun TextTitle(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = (style ?: MaterialTheme.typography.titleLarge).copy(
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            fontFamily = CabinSketchFontFamily
        )
    )
}

@Composable
fun TextTitle(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = (style ?: MaterialTheme.typography.titleLarge).copy(
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            fontFamily = CabinSketchFontFamily
        )
    )
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TextPreviews() {
    Screen {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.md), modifier = Modifier.fillMaxSize()
        ) {
            TextRegular("Manuel Lopera")
            TextBody("Manuel Lopera")
            TextTitle("Lopera y Jesús")
            HeaderSectionText("My Company")
            AmountText(15.0)
            StockProductText(0)
            StockProductText(4)
            StockProductText(14)
            StockProductText(24)
            StockOrderText(34)
        }
    }
}