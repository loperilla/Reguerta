package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.model.User
import com.reguerta.domain.model.toDomain
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 10:50
 * All rights reserved 2024
 */

class GetUserByIdUseCase @Inject constructor(private val repository: UsersCollectionService) {
    suspend operator fun invoke(id: String): Result<User> {
        return repository.getUser(id).fold(
            onSuccess = {
                Result.success(it.toDomain())
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}