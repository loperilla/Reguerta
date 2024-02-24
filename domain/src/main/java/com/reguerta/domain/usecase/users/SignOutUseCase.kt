package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.auth.AuthService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 12:25
 * All rights reserved 2024
 */
class SignOutUseCase @Inject constructor(
    private val authService: AuthService
) {

    suspend operator fun invoke() = authService.signOut()
}