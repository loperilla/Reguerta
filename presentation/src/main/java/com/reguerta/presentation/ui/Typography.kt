package com.reguerta.presentation.ui

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.ui
 * Created By Manuel Lopera on 23/1/24 at 20:20
 * All rights reserved 2024
 */

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.R

val cabinsketchFontFamily = FontFamily(
    Font(R.font.cabinsketch_regular, FontWeight.Normal),
    Font(R.font.cabinsketch_bold, FontWeight.Bold)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = TextUnit.Unspecified,
        lineHeight = TextUnit.Unspecified,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = TextUnit.Unspecified,
        lineHeight = TextUnit.Unspecified,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = TextUnit.Unspecified,
        lineHeight = TextUnit.Unspecified,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Thin,
        fontSize = TextUnit.Unspecified,
        lineHeight = TextUnit.Unspecified,
        letterSpacing = 0.sp
    )
)