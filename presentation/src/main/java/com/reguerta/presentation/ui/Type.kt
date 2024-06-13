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
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.R

val monserratFontFamily = FontFamily(
    Font(R.font.monserrat_regular, FontWeight.Normal),
    Font(R.font.monserrat_bold, FontWeight.Bold)
)

val cabinsketchFontFamily = FontFamily(
    Font(R.font.cabinsketch_regular, FontWeight.Normal),
    Font(R.font.cabinsketch_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = cabinsketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    /*
    ,
    bodyLarge = TextStyle(
        fontFamily = monserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = TEXT_SIZE_LARGE,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = monserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = TEXT_SIZE_LARGE,
        letterSpacing = 0.5.sp
    )

     */

)