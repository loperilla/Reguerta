package com.reguerta.presentation.screen.users.edit

import dagger.assisted.AssistedFactory

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_user
 * Created By Manuel Lopera on 24/2/24 at 13:07
 * All rights reserved 2024
 */

@AssistedFactory
interface EditUserViewModelFactory {
    fun create(userId: String): EditUserViewModel
}