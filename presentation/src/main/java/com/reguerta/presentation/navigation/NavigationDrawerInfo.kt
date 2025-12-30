package com.reguerta.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.composables.navigationDrawer
 * Created By Manuel Lopera on 3/2/24 at 12:37
 * All rights reserved 2024
 */

@Deprecated(
    message = "Modelo antiguo del drawer. Migrar a NavDrawerItem (route + roles) y resolver navegación fuera del modelo."
)
data class NavigationDrawerInfo(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val showInBothCase: Boolean = false,
    val showIfUserIsAdmin: Boolean = false,
    val showIfUserIsProducer: Boolean = false
)