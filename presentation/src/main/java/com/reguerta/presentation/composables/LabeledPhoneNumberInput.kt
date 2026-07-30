package com.reguerta.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.reguerta.presentation.ui.Dimens

@Composable
fun LabeledPhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String = "TELÉFONO DEL REGÜERTENSE",
    compactLabelText: String = "TLF. DEL REGÜERTENSE",
    placeholder: String = "Teléfono"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = Dimens.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var labelSize by remember { mutableStateOf(IntSize.Zero) }
        var useCompactLabel by remember(labelText, compactLabelText) { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens.Spacing.md)
                .onSizeChanged { size ->
                    if (size != labelSize) {
                        labelSize = size
                        useCompactLabel = false
                    }
                }
        ) {
            Text(
                text = if (useCompactLabel) compactLabelText else labelText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
                onTextLayout = { result ->
                    if (!useCompactLabel && result.hasVisualOverflow) {
                        useCompactLabel = true
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .widthIn(min = 80.dp, max = 120.dp)
                .wrapContentHeight()
                .align(Alignment.CenterVertically)
        ) {
            CustomPhoneNumberInput(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 9) {
                        onValueChange(newValue)
                    }
                },
                placeholder = placeholder,
                isError = value.length != 9,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
