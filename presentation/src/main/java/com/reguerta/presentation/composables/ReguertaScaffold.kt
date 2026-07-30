package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.input.nestedscroll.nestedScroll

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReguertaScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contentWindowInsets: WindowInsets = WindowInsets.systemBars,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = if (scrollBehavior != null) modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}
