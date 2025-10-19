package com.reguerta.presentation.composables

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.reguerta.presentation.ui.Dimens
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.times

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables
 * Created By Manuel Lopera on 2/3/24 at 13:12
 * All rights reserved 2024
 */

data class DropDownItem(
    val text: String
)

@Composable
fun DropdownSelectable(
    currentSelected: String,
    dropdownItems: List<DropDownItem>,
    modifier: Modifier = Modifier,
    onItemClick: (DropDownItem) -> Unit
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    var anchorWidth by remember {
        mutableStateOf(0.dp)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    ReguertaCard(
        modifier = modifier
            .onSizeChanged {
                with(density) {
                    itemHeight = it.height.toDp()
                    anchorWidth = it.width.toDp()
                }
            },
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        content = {
            Box(
                modifier = Modifier
                    .heightIn(Dimens.Size.dp36)
                    .fillMaxWidth()
                    .indication(interactionSource, LocalIndication.current)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isContextMenuVisible = true
                                with(density) { pressOffset = DpOffset(it.x.toDp(), it.y.toDp()) }
                            },
                            onPress = {
                                val press = PressInteraction.Press(it)
                                interactionSource.emit(press)
                                tryAwaitRelease()
                                interactionSource.emit(PressInteraction.Release(press))
                            }
                        )
                    }
                    .padding(Dimens.Spacing.sm)
            ) {
                TextBody(
                    text = currentSelected,
                    textSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            val estimatedMenuHeight = (dropdownItems.size * 40.dp).coerceAtMost(320.dp)
            val availableBelow = screenHeight - pressOffset.y
            val openUp = estimatedMenuHeight > availableBelow && pressOffset.y > availableBelow
            val desiredY = if (openUp) {
                // open upwards
                pressOffset.y - estimatedMenuHeight
            } else {
                // default: open downwards (aligning top near the pressed point minus anchor height)
                pressOffset.y - itemHeight
            }
            val clampedY = desiredY.coerceIn(0.dp, (screenHeight - estimatedMenuHeight).coerceAtLeast(0.dp))
            DropdownMenu(
                modifier = Modifier.width(anchorWidth),
                expanded = isContextMenuVisible,
                onDismissRequest = {
                    isContextMenuVisible = false
                },
                offset = DpOffset(0.dp, clampedY)
            ) {
                dropdownItems.forEach {
                    DropdownMenuItem(
                        onClick = {
                            onItemClick(it)
                            isContextMenuVisible = false
                        },
                        text = {
                            TextBody(
                                text = it.text,
                                textSize = MaterialTheme.typography.bodyLarge.fontSize,
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    )
}
