package com.reguerta.presentation.ui

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.ui
 * Created By Manuel Lopera on 23/1/24 at 20:20
 * All rights reserved 2024
 */

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import timber.log.Timber
import com.reguerta.presentation.ResizedTextSizes

private val LightColorScheme = lightColorScheme(
    primary = primary6DA539,
    onPrimary = onPrimaryWhite,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = textLight,
    secondary = secondary4C774C,
    onSecondary = onSecondaryWhite,
    secondaryContainer = secondBackLight,
    onSecondaryContainer = textLight,
    tertiary = tertiary4A9184,
    onTertiary = onTertiaryWhite,
    tertiaryContainer = mainBackLight,
    onTertiaryContainer = textLight,
    error = errorColor,
    onError = onPrimaryWhite,
    errorContainer = mainBackLight,
    onErrorContainer = errorColor,
    background = mainBackLight,
    onBackground = textLight,
    surface = mainBackLight,
    onSurface = textLight,
    inverseSurface = textLight,
    inverseOnSurface = mainBackLight,
    surfaceVariant = secondBackLight,
    onSurfaceVariant = textLight,
    outline = primary6DA539,
    inversePrimary = textLight,
    surfaceTint = primary6DA539
)

private val DarkColorScheme = darkColorScheme(
    primary = primary6DA539,
    onPrimary = textLight,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryWhite,
    secondary = secondary4C774C,
    onSecondary = textDark,
    secondaryContainer = secondBackDark,
    onSecondaryContainer = onPrimaryWhite,
    tertiary = tertiary4A9184,
    onTertiary = textDark,
    tertiaryContainer = secondBackDark,
    onTertiaryContainer = onPrimaryWhite,
    error = errorColor,
    onError = onPrimaryWhite,
    errorContainer = errorColor,
    onErrorContainer = mainBackDark,
    background = mainBackDark,
    onBackground = textDark,
    surface = mainBackDark,
    onSurface = textDark,
    inverseSurface = textDark,
    inverseOnSurface = mainBackDark,
    surfaceVariant = secondBackDark,
    onSurfaceVariant = textDark,
    outline = primary6DA539,
    inversePrimary = mainBackDark,
    surfaceTint = primary6DA539
)

@Composable
fun ReguertaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  //  <- Por ahora desactivado, cuando esté en ajustes, se activa
    content: @Composable () -> Unit
) {
    // Hard-disable dynamic color for now (we'll re-enable from Settings later)
    val colorScheme =
        if (darkTheme) DarkColorScheme else LightColorScheme
    Timber.i("ReguertaTheme: dynamicColor=FALSE (forced), darkTheme=%s", darkTheme)

    val window = (LocalView.current.context as Activity).window
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
    insetsController.isAppearanceLightStatusBars = !darkTheme
    insetsController.isAppearanceLightNavigationBars = !darkTheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            ResizedTextSizes()
            //ResizedDimensions()
            content()
        }
    )
}