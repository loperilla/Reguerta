package com.reguerta.presentation.composables


import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.focusable
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reguerta.presentation.ui.Dimens
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment

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
    cornerRadius: Dp = Dimens.Radius.sm,
    onItemClick: (DropDownItem) -> Unit
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var anchorWidth by remember {
        mutableStateOf(0.dp)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val density = LocalDensity.current

    ReguertaCard(
        modifier = modifier
            .onSizeChanged {
                with(density) { anchorWidth = it.width.toDp() }
            },
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        cornerRadius = cornerRadius,
        content = {
            Box(
                modifier = Modifier
                    .heightIn(min = Dimens.Components.Dropdown.anchorHeight)
                    .fillMaxWidth()
                    .semantics { role = Role.Button }
                    .focusable(true)
                    .indication(interactionSource, LocalIndication.current)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) {
                        isContextMenuVisible = true
                    }
                    .padding(
                        horizontal = Dimens.Components.Dropdown.contentPaddingHorizontal,
                        vertical = Dimens.Components.Dropdown.contentPaddingVertical
                    ),
                contentAlignment = Alignment.Center
            ) {
                TextBody(
                    text = currentSelected,
                    style = MaterialTheme.typography.bodyLarge,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    textAlignment = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenu(
                modifier = Modifier
                    .width(anchorWidth)
                    .heightIn(max = Dimens.Components.Dropdown.menuMaxHeight),
                expanded = isContextMenuVisible,
                onDismissRequest = {
                    isContextMenuVisible = false
                }
            ) {
                dropdownItems.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.height(Dimens.Components.Dropdown.itemHeight),
                        onClick = {
                            onItemClick(it)
                            isContextMenuVisible = false
                        },
                        text = {
                            TextBody(
                                text = it.text,
                                style = MaterialTheme.typography.bodyLarge,
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    )
}
