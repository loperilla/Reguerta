package com.reguerta.user

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.reguerta.presentation.screen.new_order.LastOrderScreen
import com.reguerta.presentation.screen.new_order.NewOrderState
import com.reguerta.presentation.ui.ReguertaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OrderInsetsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun orderTotalStaysAboveNavigationBar() {
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.enableEdgeToEdge()
        }
        composeRule.setContent {
            ReguertaTheme {
                LastOrderScreen(
                    state = NewOrderState(),
                    onEvent = {}
                )
            }
        }

        val navigationBarInset = ViewCompat.getRootWindowInsets(
            composeRule.activity.window.decorView
        )?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
        val density = composeRule.activity.resources.displayMetrics.density
        val rootBottom = composeRule.onRoot().getUnclippedBoundsInRoot().bottom.value * density
        val totalBottom = composeRule
            .onNode(hasText("Suma total pedido:", substring = true))
            .getUnclippedBoundsInRoot()
            .bottom
            .value * density

        assertTrue("The test device must expose a navigation bar inset", navigationBarInset > 0)
        assertTrue(
            "Order total must stay above the navigation bar",
            totalBottom <= rootBottom - navigationBarInset
        )
    }
}
