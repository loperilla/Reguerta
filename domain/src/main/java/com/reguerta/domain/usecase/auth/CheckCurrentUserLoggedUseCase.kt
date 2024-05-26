package com.reguerta.domain.usecase.auth

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.domain.model.User
import com.reguerta.domain.model.toDomain
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.auth
 * Created By Manuel Lopera on 3/3/24 at 17:21
 * All rights reserved 2024
 */

class CheckCurrentUserLoggedUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(): Result<User> {
        return authService.checkCurrentLoggedUser().fold(
            onSuccess = { model ->
                Result.success(model.toDomain())
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}