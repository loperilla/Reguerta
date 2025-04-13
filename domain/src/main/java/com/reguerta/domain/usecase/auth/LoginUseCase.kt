package com.reguerta.domain.usecase.auth

import com.reguerta.data.AuthState
import com.reguerta.data.firebase.auth.AuthService
import java.time.LocalDate
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.auth
 * Created By Manuel Lopera on 24/2/24 at 10:58
 * All rights reserved 2024
 */

class LoginUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(email: String, password: String, testDate: LocalDate? = null): Result<Boolean> {
        testDate?.let { authService.simulateCurrentDate(it) }

        return when (val result = authService.logInWithUserPassword(email, password)) {
            is AuthState.Error -> Result.failure(Exception(result.message))
            AuthState.LoggedIn -> Result.success(true)
        }
    }
}