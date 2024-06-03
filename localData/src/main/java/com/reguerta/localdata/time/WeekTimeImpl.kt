package com.reguerta.localdata.time

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.time
 * Created By Manuel Lopera on 13/3/24 at 18:25
 * All rights reserved 2024
 */

class WeekTimeImpl @Inject constructor() : WeekTime {
    override fun getCurrentWeek(): Int {
        val today = LocalDate.now()
        val weekFields = WeekFields.of(Locale("es", "ES"))
        return today.get(weekFields.weekOfWeekBasedYear())
    }

    override fun getCurrentWeekDay(): Int {
        return LocalDate.now().dayOfWeek.value
    }

    override fun isEvenCurrentWeek(): Boolean {
        return getCurrentWeek() % 2 == 0
    }
}