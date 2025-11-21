package com.reguerta.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.heightIn
import com.reguerta.presentation.ui.Dimens
import com.reguerta.domain.enums.UiType

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:57
 * All rights reserved 2024
 */

typealias BtnType = UiType

enum class ButtonLayout { Fixed, Fill }

@Composable
private fun Modifier.applyButtonWidth(
    twoButtons: Boolean,
    isDialog: Boolean,
    layout: ButtonLayout
): Modifier = when (layout) {
    ButtonLayout.Fill -> this.fillMaxWidth()
    ButtonLayout.Fixed -> this.width(
        when {
            twoButtons && isDialog -> Dimens.Components.Button.fixedTwoButtonsDialogWidth
            twoButtons -> Dimens.Components.Button.fixedTwoButtonsWidth
            else -> Dimens.Components.Button.fixedSingleWidth
        }
    )
}

@Composable
fun ReguertaButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true,
    isSingleButton: Boolean = true,
    btnType: BtnType = BtnType.INFO
) {
    val shape = RoundedCornerShape(Dimens.Radius.md)
    val finalModifier = if (isSingleButton) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }
    Button(
        onClick = onClick,
        modifier = finalModifier.heightIn(min = Dimens.Components.Button.minHeight),
        enabled = enabledButton,
        colors = Dimens.Components.Button.colors(btnType),
        shape = shape,
    ) {
        TextRegular(
            text = textButton,
            style = if (isSingleButton) Dimens.Components.Button.labelStyle else Dimens.Components.Button.secondaryLabelStyle,
            textColor = Color.Unspecified,
            modifier = Modifier.padding(
                horizontal = Dimens.Components.Button.horizontalPadding,
                vertical = Dimens.Components.Button.verticalPadding
            )
        )
    }
}

@Composable
fun InverseReguertaButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true,
    isSingleButton: Boolean = true,
    btnType: BtnType = BtnType.INFO
) {
    val shape = RoundedCornerShape(Dimens.Radius.md)
    val finalModifier = if (isSingleButton) modifier.fillMaxWidth() else modifier
    Button(
        onClick = onClick,
        modifier = finalModifier.heightIn(min = Dimens.Components.Button.minHeight),
        border = BorderStroke(Dimens.Border.thin, Dimens.Components.Button.borderColor(btnType)),
        enabled = enabledButton,
        colors = Dimens.Components.Button.inverseColors(btnType),
        shape = shape
    ) {
        TextBody(
            text = textButton,
            style = if (isSingleButton) Dimens.Components.Button.labelStyle else Dimens.Components.Button.secondaryLabelStyle,
            textColor = Color.Unspecified,
            modifier = Modifier.padding(
                horizontal = Dimens.Components.Button.horizontalPadding,
                vertical = Dimens.Components.Button.verticalPadding
            )
        )
    }
}

@Deprecated("'borderSize' y 'cornerSize' no se usan; el estilo lo gobiernan los tokens Dimens.Border/Dimens.Corner y Dimens.Components.Button.")
@Suppress("UNUSED_PARAMETER")
@Composable
fun InverseReguertaButton(
    content: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true,
    borderSize: Dp = 2.dp, // deprecated
    cornerSize: Float = 16f, // deprecated
    btnType: BtnType = BtnType.INFO
) {
    val shape = RoundedCornerShape(Dimens.Radius.md)
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = Dimens.Components.Button.minHeight),
        border = BorderStroke(Dimens.Border.thin, Dimens.Components.Button.borderColor(btnType)),
        enabled = enabledButton,
        shape = shape,
        colors = Dimens.Components.Button.inverseColors(btnType),
        content = content
    )
}

@Composable
fun ReguertaOrderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(Dimens.Radius.md)
    val baseContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val disabledContainer = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f)
    val disabledContent = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    val colors = ButtonDefaults.buttonColors(
        containerColor = baseContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = disabledContainer,
        disabledContentColor = disabledContent
    )

    val decoratedModifier =
        if (!enabled) {
            modifier.border(
                BorderStroke(Dimens.Border.thin, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)),
                shape = shape
            )
        } else {
            modifier.border(
                BorderStroke(Dimens.Border.thin, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)),
                shape = shape
            )
        }

    Button(
        onClick = onClick,
        modifier = decoratedModifier,
        shape = shape,
        enabled = enabled,
        colors = colors
    ) {
        TextBody(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textColor = if (enabled) MaterialTheme.colorScheme.primary else disabledContent,
            modifier = Modifier.padding(Dimens.Spacing.sm)
        )
        if (!enabled) {
            Spacer(modifier = Modifier.width(Dimens.Spacing.lg))
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun ReguertaIconButton(
    iconButton: ImageVector,
    onClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        enabled = enabledButton,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = contentColor,
        )
    ) {
        Icon(
            iconButton,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ReguertaFullButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    btnType: BtnType = BtnType.INFO,
    twoButtons: Boolean = false,
    isDialog: Boolean = false,
    layout: ButtonLayout = ButtonLayout.Fixed,
) {
    val shape = RoundedCornerShape(Dimens.Radius.lg)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = Dimens.Components.Button.colors(btnType),
        modifier = modifier
            .heightIn(min = Dimens.Components.Button.defaultHeight)
            .applyButtonWidth(twoButtons = twoButtons, isDialog = isDialog, layout = layout)
    ) {
        TextRegular(
            text = textButton,
            style = Dimens.Components.Button.labelStyle,
            textColor = Color.Unspecified,
            modifier = Modifier.padding(
                horizontal = Dimens.Components.Button.horizontalPadding,
                vertical = Dimens.Components.Button.verticalPadding
            )
        )
    }
}

@Composable
fun ReguertaFlatButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    btnType: BtnType = BtnType.INFO,
    twoButtons: Boolean = false,
    isDialog: Boolean = false,
    layout: ButtonLayout = ButtonLayout.Fixed,
) {
    val shape = RoundedCornerShape(Dimens.Radius.lg)
    val isError = btnType == BtnType.ERROR

    val colors = ButtonDefaults.buttonColors(
        containerColor = if (isError) Color.Transparent else Dimens.Components.Button.flatContainerColor,
        contentColor = if (!enabled) Dimens.Components.Button.disabledContentColor
                       else if (isError) MaterialTheme.colorScheme.error
                       else Dimens.Components.Button.flatContentColor,
        disabledContainerColor = Dimens.Components.Button.disabledContainerColor,
        disabledContentColor = Dimens.Components.Button.disabledContentColor
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        border = BorderStroke(Dimens.Border.thin, Dimens.Components.Button.borderColor(btnType)),
        modifier = modifier
            .heightIn(min = Dimens.Components.Button.defaultHeight)
            .applyButtonWidth(twoButtons = twoButtons, isDialog = isDialog, layout = layout)
    ) {
        TextBody(
            text = textButton,
            style = Dimens.Components.Button.labelStyle,
            textColor = Color.Unspecified,
            modifier = Modifier.padding(
                horizontal = Dimens.Components.Button.horizontalPadding,
                vertical = Dimens.Components.Button.verticalPadding
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaButtonPreview() {
    Screen {
        Column(
            modifier = Modifier.padding(Dimens.Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Dimens.Spacing.sm)
        ) {
            ReguertaButton(
                textButton = "Button",
                onClick = {}
            )
            ReguertaButton(
                textButton = "Button",
                onClick = {},
                enabledButton = false
            )
            InverseReguertaButton(
                textButton = "Button",
                onClick = {}
            )
            InverseReguertaButton(
                textButton = "Button",
                onClick = {},
                enabledButton = false
            )
            InverseReguertaButton(
                content = {
                    TextBody(
                        text = "con content",
                        style = Dimens.Components.Button.secondaryLabelStyle,
                        textColor = Color.Unspecified
                    )
                },
                onClick = {},
                enabledButton = false,
            )
            ReguertaIconButton(
                iconButton = Icons.Filled.Delete,
                onClick = {},
                contentColor = Color.Red,
                enabledButton = false
            )
            ReguertaIconButton(
                iconButton = Icons.Filled.Edit,
                onClick = {},
                contentColor = MaterialTheme.colorScheme.primary,
                enabledButton = false
            )
        }
    }
}
