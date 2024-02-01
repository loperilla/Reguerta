package com.reguerta.presentation.type

import android.util.Patterns


/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.type
 * Created By Manuel Lopera on 31/1/24 at 11:22
 * All rights reserved 2024
 */

typealias Email = String

val Email.isValidEmail: Boolean
    get() = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()