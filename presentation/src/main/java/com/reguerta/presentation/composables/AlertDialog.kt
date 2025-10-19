package com.reguerta.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.reguerta.presentation.ui.Dimens
import com.reguerta.domain.enums.UiType

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 9/3/24 at 10:25
 * All rights reserved 2024
 */

/**
 * ReguertaAlertDialog (tokenizada)
 * Variante de alto nivel que aplica los tamaños/estilos definidos en Dimens.Components.Dialog.
 * Usa esta función para no repetir tamaños en cada llamada.
 */
@Composable
fun ReguertaAlertDialog(
    onDismissRequest: () -> Unit,
    icon: ImageVector? = null,
    titleText: String? = null,
    bodyText: String? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    dismissText: String? = null,
    onDismissButton: (() -> Unit)? = null,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContainerColor: Color? = null,
    iconInnerColor: Color? = null,
    iconContentColor: Color? = null,
    type: UiType = UiType.INFO,
) {
    val colorSet = Dimens.Components.Dialog.colorsFor(type)
    val badgeContainer = iconContainerColor ?: colorSet.badgeContainer
    val badgeInner = iconInnerColor ?: colorSet.badgeInner
    val iconTint = iconContentColor ?: colorSet.icon

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            if (icon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(Dimens.Components.Dialog.badgeSize)
                        .background(badgeContainer, shape = CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.Components.Dialog.badgeSize * Dimens.Components.Dialog.innerBadgeFraction)
                            .background(badgeInner, shape = CircleShape)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(Dimens.Components.Dialog.iconSize)
                    )
                }
            }
        },
        title = {
            if (!titleText.isNullOrBlank()) {
                TextTitle(
                    text = titleText,
                    style = Dimens.Components.Dialog.titleStyle,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            if (!bodyText.isNullOrBlank()) {
                TextBody(
                    text = bodyText,
                    style = Dimens.Components.Dialog.bodyStyle,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            if (!confirmText.isNullOrBlank() && onConfirm != null) {
                ReguertaButton(
                    textButton = confirmText,
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Components.Dialog.verticalPadding)
                )
            }
        },
        dismissButton = {
            if (!dismissText.isNullOrBlank() && onDismissButton != null) {
                InverseReguertaButton(
                    textButton = dismissText,
                    onClick = onDismissButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Components.Dialog.verticalPadding)
                )
            }
        },
        containerColor = containerColor,
        iconContentColor = iconTint,
    )
}

@Composable
fun ReguertaAlertDialog(
    onDismissRequest: () -> Unit,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit = {},
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor
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
            onDismissRequest = { },
            icon = Icons.Default.Info,
            titleText = "Cerrar sesión",
            bodyText = "¿Estás seguro que quieres cerrar la sesión?",
            confirmText = "Cerrar sesión",
            onConfirm = { },
            dismissText = "Volver",
            onDismissButton = { },
            containerColor = MaterialTheme.colorScheme.background,
            type = com.reguerta.domain.enums.UiType.INFO
        )
    }
}

@Preview
@Composable
fun ReguertaNoButtonDialogPrev() {
    Screen {
        ReguertaAlertDialog(
            onDismissRequest = { },
            icon = Icons.Default.Info,
            titleText = "Cerrar sesión",
            bodyText = "¿Estás seguro que quieres cerrar la sesión?",
            confirmText = null,
            onConfirm = null,
            dismissText = null,
            onDismissButton = null,
            containerColor = MaterialTheme.colorScheme.background,
            type = com.reguerta.domain.enums.UiType.INFO
        )
    }
}