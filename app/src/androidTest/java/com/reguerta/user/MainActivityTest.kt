package com.reguerta.user

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.reguerta.presentation.MainNavigation
import com.reguerta.presentation.ui.Routes
import com.reguerta.testutils.ReguertaAndroidTest
import com.reguerta.user.robots.FirstScreenRobot
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/*****
 * Project: Reguerta
 * From: com.reguerta.user
 * Created By Manuel Lopera on 30/1/24 at 10:54
 * All rights reserved 2024
 */
@HiltAndroidTest
class MainActivityTest : ReguertaAndroidTest() {

    @get:Rule
    val composeRule: ReguertaComposeRule = createAndroidComposeRule<MainActivity>()
    private lateinit var navController: TestNavHostController

    @Before
    override fun setUp() {
        super.setUp()
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MainNavigation(navController = navController, startDestination = Routes.AUTH.FIRST_SCREEN.route)
        }
    }

    @Test
    fun firstScreenIsDisplayed() {
        FirstScreenRobot(composeRule)
            .assertIsDisplayed()

        navController.assertCurrentRouteName(Routes.AUTH.LOGIN.route)
    }

    @Test
    fun loginScreenIsDisplayed() {
        FirstScreenRobot(composeRule)
            .assertIsDisplayed()
            .navigateToLogin()

        navController.assertCurrentRouteName(Routes.AUTH.LOGIN.route)
    }
}