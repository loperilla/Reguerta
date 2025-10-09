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
import com.reguerta.presentation.LocalTextSizes
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_LARGE
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.PADDING_ZERO

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 24/1/24 at 16:57
 * All rights reserved 2024
 */

// A ver donde coloco este enum y las funciones auxiliares o las dejo aquí
enum class BtnType {
    INFO, ERROR
}

@Composable
fun getContainerColor(btnType: BtnType): Color = when(btnType) {
    BtnType.INFO -> MaterialTheme.colorScheme.primary
    BtnType.ERROR -> MaterialTheme.colorScheme.error
}

@Composable
fun getBorderColor(btnType: BtnType): Color = getContainerColor(btnType)

@Composable
fun ReguertaButton(
    textButton: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true,
    isSingleButton: Boolean = true,
    btnType: BtnType = BtnType.INFO
) {
    val finalModifier = if (isSingleButton) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }
    val sizes = LocalTextSizes.current
    Button(
        onClick = onClick,
        modifier = finalModifier,
        enabled = enabledButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = getContainerColor(btnType),
            disabledContainerColor = Color.Gray.copy(alpha = 0.15f),
            contentColor = when (btnType) {
                BtnType.INFO -> MaterialTheme.colorScheme.onPrimary
                BtnType.ERROR -> MaterialTheme.colorScheme.onError
            },
            disabledContentColor = Color.Gray
        )
    ) {
        TextRegular(
            text = textButton,
            textSize = if (isSingleButton) sizes.singleBtn else sizes.pairBtn,
            textColor = if (enabledButton) MaterialTheme.colorScheme.background else Color.Gray,
            modifier = Modifier.padding(horizontal = PADDING_ZERO, vertical = PADDING_EXTRA_SMALL)
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
    val sizes = LocalTextSizes.current
    Button(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, getBorderColor(btnType)),
        enabled = enabledButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        TextBody(
            text = textButton,
            textSize = if (isSingleButton) sizes.singleBtn else sizes.pairBtn,
            textColor = when (btnType) {
                BtnType.INFO -> MaterialTheme.colorScheme.primary
                BtnType.ERROR -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier.padding(PADDING_EXTRA_SMALL)
        )
    }
}

@Composable
fun InverseReguertaButton(
    content: @Composable RowScope.() -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledButton: Boolean = true,
    borderSize: Dp = 2.dp,
    cornerSize: Float = 16f,
    btnType: BtnType = BtnType.INFO
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(borderSize, getBorderColor(btnType)),
        enabled = enabledButton,
        shape = RoundedCornerShape(cornerSize.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
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
    val shape = RoundedCornerShape(32.dp)
    val baseContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) // un poco más sólido que antes
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
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)),
                shape = shape
            )
        } else {
            modifier.border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)),
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
            textSize = LocalTextSizes.current.special,
            textColor = if (enabled) MaterialTheme.colorScheme.primary else disabledContent,
            modifier = Modifier.padding(PADDING_SMALL)
        )
        if (!enabled) {
            Spacer(modifier = Modifier.width(PADDING_LARGE))
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReguertaButtonPreview() {
    Screen {
        Column(
            modifier = Modifier.padding(PADDING_SMALL),
            verticalArrangement = Arrangement.spacedBy(PADDING_SMALL)
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
                        textSize = LocalTextSizes.current.small,
                        textColor = MaterialTheme.colorScheme.onSurface
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
