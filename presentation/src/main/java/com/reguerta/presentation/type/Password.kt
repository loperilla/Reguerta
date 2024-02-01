package com.reguerta.presentation.type

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.type
 * Created By Manuel Lopera on 31/1/24 at 11:21
 * All rights reserved 2024
 */

typealias Password = String

val Password.isValidPassword: Boolean
    get() = this.isNotEmpty() && this.length >= 6