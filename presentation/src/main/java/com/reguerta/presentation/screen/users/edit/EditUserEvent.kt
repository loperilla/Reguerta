package com.reguerta.presentation.screen.users.edit


/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_user
 * Created By Manuel Lopera on 24/2/24 at 13:02
 * All rights reserved 2024
 */
sealed class EditUserEvent {
    data class EmailInputChanges(val inputValue: String) : EditUserEvent()
    data class SurnameInputChanges(val inputValue: String) : EditUserEvent()
    data class PhoneNumberInputChanges(val inputValue: String) : EditUserEvent()
    data class CompanyNameInputChanges(val inputValue: String) : EditUserEvent()
    data class NameInputChanges(val inputValue: String) : EditUserEvent()
    data class ToggledIsAdmin(val newValue: Boolean) : EditUserEvent()
    data class ToggledIsProducer(val newValue: Boolean) : EditUserEvent()
    data class ToggledIsShoppingProducer(val newValue: Boolean) : EditUserEvent()
    data object EditUser : EditUserEvent()
    data object GoBack : EditUserEvent()
}