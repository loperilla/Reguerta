package com.reguerta.presentation.screen.users

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 6/2/24 at 17:09
 * All rights reserved 2024
 */
sealed class UserScreenEvent {
    data object LoadUsers : UserScreenEvent()
    data class ToggleProducer(val idToggled: String) : UserScreenEvent()
    data class ToggleAdmin(val idToggled: String) : UserScreenEvent()
    data object GoOut : UserScreenEvent()
    data class ShowAreYouSureDialog(val idToDelete: String) : UserScreenEvent()
    data object HideAreYouSureDialog : UserScreenEvent()
    data object ConfirmDelete : UserScreenEvent()
}
