package com.reguerta.presentation.screen.users.add

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_user
 * Created By Manuel Lopera on 17/2/24 at 11:58
 * All rights reserved 2024
 */
sealed class AddUserEvent {
    data class EmailInputChanges(val inputValue: String) : AddUserEvent()
    data class SurnameInputChanges(val inputValue: String) : AddUserEvent()
    data class CompanyNameInputChanges(val inputValue: String) : AddUserEvent()
    data class NameInputChanges(val inputValue: String) : AddUserEvent()
    data class PhoneNumberInputChanges(val inputValue: String) : AddUserEvent()
    data class ToggledIsAdmin(val newValue: Boolean) : AddUserEvent()
    data class ToggledIsProducer(val newValue: Boolean) : AddUserEvent()
    data object AddUser : AddUserEvent()
    data object GoBack : AddUserEvent()
}