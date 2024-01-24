package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.reguerta.presentation.ui.ReguertaTheme

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:12
 * All rights reserved 2024
 */

@Composable
fun Screen(content: @Composable () -> Unit) {
    ReguertaTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }
    }
}