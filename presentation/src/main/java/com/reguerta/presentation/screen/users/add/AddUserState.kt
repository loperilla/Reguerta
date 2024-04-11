package com.reguerta.presentation.screen.users.add

import com.reguerta.presentation.type.Email

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_user
 * Created By Manuel Lopera on 17/2/24 at 11:57
 * All rights reserved 2024
 */
data class AddUserState(
    val name: String = "",
    val surname: String = "",
    val companyName: String = "",
    val phoneNumber: String = "",
    val email: Email = "",
    val isProducer: Boolean = false,
    val isAdmin: Boolean = false,
    val typeProducer: String = "",
    val typeConsumer: String = "normal",
    val available: Boolean = true,
    val goOut: Boolean = false,
    val isButtonEnabled: Boolean = false
)
