package com.reguerta.domain.usecase.auth

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.domain.AdminProducerUser
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.auth
 * Created By Manuel Lopera on 3/3/24 at 17:21
 * All rights reserved 2024
 */
class CheckAdminProducerUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(): AdminProducerUser {
        return authService.checkIfUserIsAdminAndProducer()
    }
}