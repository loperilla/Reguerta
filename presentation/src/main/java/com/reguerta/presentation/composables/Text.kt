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
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.labelMedium.fontSize,
    textColor: Color = Color.Unspecified,
    style: TextStyle? = null
) {
    val finalStyle = style ?: MaterialTheme.typography.labelMedium.copy(fontSize = textSize, fontWeight = FontWeight.Normal)
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        style = finalStyle
    )
}

@Composable
fun TextBody(
    text: String,
    modifier: Modifier = Modifier,
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    style: TextStyle? = null
) {
    val finalStyle = style ?: MaterialTheme.typography.bodyLarge.copy(fontSize = textSize, fontWeight = fontWeight)
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = finalStyle
    )
}

@Composable
fun TextBody(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    textColor: Color = Color.Unspecified,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    val finalStyle = style ?: MaterialTheme.typography.bodyLarge.copy(fontSize = textSize, fontWeight = FontWeight.Normal)
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = finalStyle
    )
}

@Composable
fun StockProductText(
    stockCount: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    style: TextStyle? = null
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> MaterialTheme.colorScheme.error
        in 1..10 -> LowStock
        else -> MaterialTheme.colorScheme.onSurface
    }
    TextBody(
        text = "Stock: $stockCount",
        modifier = modifier,
        textColor = colorToDraw,
        style = style ?: MaterialTheme.typography.bodyMedium.copy(fontSize = textSize)
    )
}

@Composable
fun StockOrderText(
    stockCount: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    style: TextStyle? = null
) {
    val colorToDraw: Color = when (stockCount) {
        0 -> MaterialTheme.colorScheme.error
        in 1..10 -> LowStock
        in 11..20 -> MaterialTheme.colorScheme.onSurface
        else -> Color.Transparent
    }
    TextBody(
        text = "Quedan: $stockCount uds.",
        modifier = modifier,
        textColor = colorToDraw,
        style = style ?: MaterialTheme.typography.bodyMedium.copy(fontSize = textSize)
    )
}

@Composable
fun AmountText(
    amount: Double,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle? = null,
    amountStyle: TextStyle? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextTitle(
            text = "Total: ",
            textColor = MaterialTheme.colorScheme.primary,
            style = titleStyle ?: MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        TextBody(
            text = String.format(Locale.getDefault(), "%.2f €", amount),
            textColor = MaterialTheme.colorScheme.onSurface,
            style = amountStyle ?: MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun HeaderSectionText(
    text: String,
    modifier: Modifier = Modifier,
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.primary,
    textAlignment: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    padding: Dp = Dimens.Spacing.sm,
    style: TextStyle? = null
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(Dimens.Components.Card.cornerRadius))
            .fillMaxWidth()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        val finalStyle = style ?: MaterialTheme.typography.titleLarge.copy(fontSize = textSize, fontWeight = fontWeight)
        Text(
            text = text,
            color = textColor,
            textAlign = textAlignment,
            style = finalStyle
        )
    }
}

@Composable
fun TextTitle(
    text: String,
    modifier: Modifier = Modifier,
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    val finalStyle = style ?: MaterialTheme.typography.titleLarge.copy(fontSize = textSize, fontWeight = FontWeight.Bold)
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = finalStyle
    )
}

@Composable
fun TextTitle(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    // DEPRECATED PARAM: usa 'style' (roles tipográficos). Este parámetro se retirará.
    textSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    textAlignment: TextAlign? = null,
    style: TextStyle? = null
) {
    val finalStyle = style ?: MaterialTheme.typography.titleLarge.copy(fontSize = textSize, fontWeight = FontWeight.Bold)
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlignment,
        style = finalStyle
    )
}
