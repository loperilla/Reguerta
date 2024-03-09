package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reguerta.presentation.ui.DialogBackground
import com.reguerta.presentation.ui.PrimaryColor
import com.reguerta.presentation.ui.Text

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 9/3/24 at 10:25
 * All rights reserved 2024
 */

@Composable
fun ReguertaAlertDialog(
    onDismissRequest: () -> Unit,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    containerColor: Color = DialogBackground,
    iconContentColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    AlertDialog(
        icon = icon,
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        containerColor = containerColor,
        iconContentColor = iconContentColor
    )
}

@Preview
@Composable
fun ReguertaAlertDialogPrev() {
    Screen {
        ReguertaAlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "ExitApp",
                    tint = PrimaryColor,
                    modifier = Modifier
                        .size(48.dp)
                )
            },
            onDismissRequest = {
//                onEvent(HomeEvent.HideDialog)
            },
            text = {
                TextBody(
                    text = "¿Seguro que quieres cerrar la sesión?",
                    textSize = 18.sp,
                    textColor = Text
                )
            },
            title = {
                TextTitle(
                    text = "Cerrar sesión",
                    textSize = 18.sp,
                    textColor = Text
                )
            },
            confirmButton = {
                ReguertaButton(
                    textButton = "Cerrar sesión",
                    onClick = {
//                        onEvent(HomeEvent.GoOut)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            dismissButton = {
                InverseReguertaButton(
                    textButton = "Volver",
                    onClick = {
//                        onEvent(HomeEvent.HideDialog)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            containerColor = DialogBackground,
            iconContentColor = MaterialTheme.colorScheme.inversePrimary,
        )
    }
}