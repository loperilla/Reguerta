package com.reguerta.domain.usecase.auth

import com.reguerta.data.firebase.auth.AuthService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.auth
 * Created By Manuel Lopera on 7/4/24 at 16:03
 * All rights reserved 2024
 */
class SendRecoveryPasswordEmailUseCase @Inject constructor(
    private val authService: AuthService
) {

    suspend operator fun invoke(email: String) = authService.sendRecoveryPasswordEmail(email)
}