package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier

/**
 * ReguertaScaffold
 *
 * Punto único de verdad para nuestra política de insets y contenedor de pantalla.
 * Para usar en todas las pantallas en lugar de Scaffold de Material3 para garantizar
 * que siempre se apliquen los mismos insets (por defecto, systemBars).
 *
 * Recomendación: envolver la pantalla con `Screen { ... }` para tema/surface,
 * y dentro utilizar `ReguertaScaffold { innerPadding -> ... }`.
 */
@Composable
fun ReguertaScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}
