package com.reguerta.presentation.screen.users

import com.reguerta.presentation.model.User

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 6/2/24 at 17:09
 * All rights reserved 2024
 */
data class UserScreenState(
    val isLoading: Boolean = false,
    val goOut: Boolean = false,
    val userList: List<User> = emptyList()
)
