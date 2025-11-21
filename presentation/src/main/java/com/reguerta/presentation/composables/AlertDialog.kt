package com.reguerta.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import com.reguerta.presentation.ui.Dimens
import com.reguerta.domain.enums.UiType

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 9/3/24 at 10:25
 * All rights reserved 2024
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
    type: UiType = UiType.INFO,
    containerColor: Color = MaterialTheme.colorScheme.background
) {
    val hasConfirm = !confirmText.isNullOrBlank() && onConfirm != null
    val hasDismiss = !dismissText.isNullOrBlank() && onDismissButton != null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(Dimens.Components.Dialog.widthRatio),

        icon = {
            if (icon != null) {
                val accent = when (type) {
                    UiType.INFO -> MaterialTheme.colorScheme.primary
                    UiType.ERROR -> MaterialTheme.colorScheme.error
                    UiType.WARNING -> MaterialTheme.colorScheme.error
                }
                val outer = accent.copy(alpha = 0.2f)
                val outerSize = Dimens.Components.Dialog.badgeSize

                Box(
                    modifier = Modifier
                        .size(outerSize)
                        .background(outer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(Dimens.Components.Dialog.iconSize)
                    )
                }
            }
        },

        // Título 22sp bold centrado (token)
        title = {
            if (!titleText.isNullOrBlank()) {
                TextTitle(
                    text = titleText,
                    style = Dimens.Components.Dialog.titleStyle,
                    textColor = Color.Unspecified,
                    textAlignment = TextAlign.Center
                )
            }
        },

        // Cuerpo 16sp centrado con lineHeight (token)
        text = {
            if (!bodyText.isNullOrBlank()) {
                TextBody(
                    text = bodyText,
                    style = Dimens.Components.Dialog.bodyStyle,
                    textColor = Color.Unspecified,
                    textAlignment = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        // Botón principal: Full + fill width, colores por UiType
        confirmButton = {
            when {
                hasConfirm && hasDismiss -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ReguertaFlatButton(
                            textButton = dismissText,
                            onClick = onDismissButton,
                            btnType = type,
                            layout = ButtonLayout.Fill,
                            modifier = Modifier.weight(1f)
                        )
                        ReguertaFullButton(
                            textButton = confirmText,
                            onClick = onConfirm,
                            btnType = type,
                            layout = ButtonLayout.Fill,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                hasConfirm -> {
                    ReguertaFullButton(
                        textButton = confirmText,
                        onClick = onConfirm,
                        btnType = type,
                        layout = ButtonLayout.Fill,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.Components.Dialog.verticalPadding)
                    )
                }
                else -> { /* no-op to satisfy M3 requirement */ }
            }
        },

        // Botón secundario (opcional): Flat + fill width, colores por UiType
        dismissButton = {
            if (hasDismiss && !hasConfirm) {
                ReguertaFlatButton(
                    textButton = dismissText,
                    onClick = onDismissButton,
                    btnType = type,
                    layout = ButtonLayout.Fill,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.Components.Dialog.verticalPadding)
                )
            }
        },

        // Look iOS-friendly pero Material3
        containerColor = containerColor,
        shape = RoundedCornerShape(Dimens.Radius.lg),
        tonalElevation = 0.dp,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

/**
 * Overload slot-based para casos avanzados.
 * Aplica forma/tonal/contenerdor por defecto, pero deja a los slots el contenido.
 */
@Composable
fun ReguertaAlertDialog(
    onDismissRequest: () -> Unit,
    icon: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.background
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(Dimens.Components.Dialog.widthRatio),
        icon = icon,
        title = title,
        text = text,
        confirmButton = (confirmButton ?: {}),
        dismissButton = dismissButton,
        containerColor = containerColor,
        shape = RoundedCornerShape(Dimens.Radius.lg),
        tonalElevation = 0.dp,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}