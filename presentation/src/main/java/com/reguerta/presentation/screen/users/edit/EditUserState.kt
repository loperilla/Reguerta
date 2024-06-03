package com.reguerta.presentation.screen.users.edit

import com.reguerta.presentation.type.Email

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_user
 * Created By Manuel Lopera on 24/2/24 at 13:03
 * All rights reserved 2024
 */

data class EditUserState(
    val name: String = "",
    val surname: String = "",
    val companyName: String = "",
    val email: Email = "",
    val phoneNumber: String = "",
    val isProducer: Boolean = false,
    val isAdmin: Boolean = false,
    val typeProducer: String = "",
    val typeConsumer: String = "normal",
    val available: Boolean = true,
    val goOut: Boolean = false,
    val isButtonEnabled: Boolean = false
)
