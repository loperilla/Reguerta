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

val CabinSketchFontFamily = FontFamily(
    Font(R.font.cabinsketch_regular, FontWeight.Normal),
    Font(R.font.cabinsketch_bold, FontWeight.Bold)
)

val ReguertaTypography = Typography(
    /** displayLarge - displayLarge is the largest display text. */
    displayLarge = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    /** displayMedium - displayMedium is the second largest display text. */
    displayMedium = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    /** displaySmall - displaySmall is the smallest display text. */
    displaySmall = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),

    /** headlineLarge - headlineLarge is the largest headline, reserved for short, important text or numerals. For headlines, you can choose an expressive font, such as a display, handwritten, or script style. These unconventional font designs have details and intricacy that help attract the eye. */
    headlineLarge = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    /** headlineMedium - headlineMedium is the second largest headline, reserved for short, important text or numerals. For headlines, you can choose an expressive font, such as a display, handwritten, or script style. These unconventional font designs have details and intricacy that help attract the eye. */
    headlineMedium = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    /** headlineSmall - headlineSmall is the smallest headline, reserved for short, important text or numerals. For headlines, you can choose an expressive font, such as a display, handwritten, or script style. These unconventional font designs have details and intricacy that help attract the eye. */
    headlineSmall = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    /** titleLarge - titleLarge is the largest title, and is typically reserved for medium-emphasis text that is shorter in length. Serif or sans serif typefaces work well for subtitles. */
    titleLarge = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    /** titleMedium - titleMedium is the second largest title, and is typically reserved for medium-emphasis text that is shorter in length. Serif or sans serif typefaces work well for subtitles. */
    titleMedium = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    /** titleSmall - titleSmall is the smallest title, and is typically reserved for medium-emphasis text that is shorter in length. Serif or sans serif typefaces work well for subtitles. */
    titleSmall = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),

    /** bodyLarge - bodyLarge is the largest body, and is typically used for long-form writing as it works well for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended. */
    bodyLarge = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    /** bodyMedium - bodyMedium is the second largest body, and is typically used for long-form writing as it works well for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended. */
    bodyMedium = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    /** bodySmall - bodySmall is the smallest body, and is typically used for long-form writing as it works well for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended. */
    bodySmall = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),

    /** labelLarge - labelLarge text is a call to action used in different types of buttons (such as text, outlined and contained buttons) and in tabs, dialogs, and cards. Button text is typically sans serif, using all caps text. */
    labelLarge = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    /** labelMedium - labelMedium is one of the smallest font sizes. It is used sparingly to annotate imagery or to introduce a headline. */
    labelMedium = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    /** labelSmall - labelSmall is one of the smallest font sizes. It is used sparingly to annotate imagery or to introduce a headline. */
    labelSmall = TextStyle(
        fontFamily = CabinSketchFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)
