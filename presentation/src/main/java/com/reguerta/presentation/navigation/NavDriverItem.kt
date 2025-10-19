package com.reguerta.presentation.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserRole { User, Admin, Producer }

@Immutable
data class NavDrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String,                         // en vez de onClick
    val visibleFor: Set<UserRole> = setOf(UserRole.User, UserRole.Admin, UserRole.Producer)
)

fun NavDrawerItem.isVisibleFor(role: UserRole) = role in visibleFor