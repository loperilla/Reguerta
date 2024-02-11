package com.reguerta.user.robots

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.reguerta.user.ReguertaComposeRule

/*****
 * Project: Reguerta
 * From: com.reguerta.user.robots
 * Created By Manuel Lopera on 30/1/24 at 11:14
 * All rights reserved 2024
 */
class FirstScreenRobot(
    private val composeRule: ReguertaComposeRule
) {
    fun assertIsDisplayed(): FirstScreenRobot {
        composeRule
            .onNodeWithText("La RegÜerta")
            .assertIsDisplayed()

        return this
    }

    fun navigateToLogin(): FirstScreenRobot {
        composeRule
            .onNodeWithText("Iniciar Sesión")
            .performClick()

        return this
    }
}