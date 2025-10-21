package com.reguerta.domain.usecase.week

import com.reguerta.domain.time.ClockProvider
import java.time.DayOfWeek
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.week
 * Created By Manuel Lopera on 25/3/24 at 19:26
 * All rights reserved 2024
 */


class GetCurrentDayOfWeekUseCase @Inject constructor(
    private val clock: ClockProvider
) {
    operator fun invoke(): DayOfWeek = clock.today().dayOfWeek
}