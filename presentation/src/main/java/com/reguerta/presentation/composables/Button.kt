package com.reguerta.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.reguerta.presentation.ui.PADDING_EXTRA_SMALL
import com.reguerta.presentation.ui.PADDING_SMALL
import com.reguerta.presentation.ui.TEXT_SIZE_SINGLE_BTN
import com.reguerta.presentation.ui.TEXT_SIZE_PAIR_BTN
import com.reguerta.presentation.ui.TEXT_SIZE_SMALL

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
            textSize = if (isSingleButton) TEXT_SIZE_SINGLE_BTN else TEXT_SIZE_PAIR_BTN,
            textColor = if (enabledButton) MaterialTheme.colorScheme.background else Color.Gray,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = PADDING_EXTRA_SMALL)
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
    Button(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, getBorderColor(btnType)),
        enabled = enabledButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        TextBody(
            text = textButton,
            textSize = if (isSingleButton) TEXT_SIZE_SINGLE_BTN else TEXT_SIZE_PAIR_BTN,
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
        shape = RoundedCornerShape(cornerSize),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        content = content
    )
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
                        textSize = TEXT_SIZE_SMALL,
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
