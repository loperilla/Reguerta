package com.reguerta.user

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.reguerta.presentation.composables.ReguertaAlertDialog
import com.reguerta.presentation.screen.users.add.AddUserScreen
import com.reguerta.presentation.screen.users.add.AddUserState
import com.reguerta.presentation.ui.ReguertaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AdaptiveContentTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dialogActionsRemainSingleLineWithLargeText() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 1.3f)
            ) {
                ReguertaTheme {
                    ReguertaAlertDialog(
                        onDismissRequest = {},
                        titleText = "Cerrar sesión",
                        bodyText = "¿Estás seguro que quieres cerrar la sesión?",
                        confirmText = "Confirmar",
                        onConfirm = {},
                        dismissText = "Volver",
                        onDismissButton = {}
                    )
                }
            }
        }

        val dismissBounds = composeRule
            .onNode(hasText("Volver"))
            .getUnclippedBoundsInRoot()
        val confirmBounds = composeRule
            .onNode(hasText("Confirmar"))
            .getUnclippedBoundsInRoot()

        val dismissHeight = dismissBounds.bottom - dismissBounds.top
        val confirmHeight = confirmBounds.bottom - confirmBounds.top

        assertEquals(dismissHeight.value, confirmHeight.value, 0.5f)
    }

    @Test
    fun phoneLabelRemainsSingleLineWithLargeText() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 1.3f)
            ) {
                ReguertaTheme {
                    AddUserScreen(
                        state = AddUserState(),
                        onEvent = {}
                    )
                }
            }
        }

        val labelBounds = composeRule
            .onNode(
                hasText("TELÉFONO DEL REGÜERTENSE") or
                    hasText("TLF. DEL REGÜERTENSE")
            )
            .getUnclippedBoundsInRoot()

        val labelHeight = labelBounds.bottom - labelBounds.top

        assertTrue("Phone label must stay on one line", labelHeight <= 28.dp)
    }
}
