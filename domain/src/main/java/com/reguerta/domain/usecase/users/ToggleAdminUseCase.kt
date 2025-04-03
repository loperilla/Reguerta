package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 12:04
 * All rights reserved 2024
 */

class ToggleAdminUseCase @Inject constructor(private val repository: UsersCollectionService) {
    suspend operator fun invoke(id: String, newValue: Boolean) {
        repository.toggleAdmin(id, newValue)
    }
}