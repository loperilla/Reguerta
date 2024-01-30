package com.reguerta.user

import androidx.navigation.NavController
import org.junit.Assert

/*****
 * Project: Reguerta
 * From: com.reguerta.user
 * Created By Manuel Lopera on 30/1/24 at 11:51
 * All rights reserved 2024
 */

fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    Assert.assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}