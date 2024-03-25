package com.reguerta.domain.usecase.week

import com.reguerta.data.firebase.auth.AuthService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.week
 * Created By Manuel Lopera on 25/3/24 at 19:26
 * All rights reserved 2024
 */
class GetCurrentWeekDayUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(): Int = authService.getCurrentWeek()
}