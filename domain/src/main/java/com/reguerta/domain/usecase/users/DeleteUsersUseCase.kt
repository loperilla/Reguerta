package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 10:56
 * All rights reserved 2024
 */

class DeleteUsersUseCase @Inject constructor(
    private val repository: UsersCollectionService
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return try {
            repository.deleteUser(id)
            Result.success(true)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
