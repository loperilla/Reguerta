package com.reguerta.presentation.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ReguertaCircularProgress(
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        // Esto ayuda a mantener la animación activa
    }

    CircularProgressIndicator(
        modifier = modifier
            .size(64.dp)
            .border(2.dp, MaterialTheme.colorScheme.onPrimary), // Para pruebas
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 8.dp
    )
}