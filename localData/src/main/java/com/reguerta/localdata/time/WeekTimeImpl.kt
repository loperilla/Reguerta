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
    private var overrideDate: LocalDate? = null

    override fun setTestDate(date: LocalDate) {
        overrideDate = date
    }

    override fun clearTestDate() {
        overrideDate = null
    }

    override fun getCurrentWeek(): Int {
        val today = overrideDate ?: LocalDate.now()
        val weekFields = WeekFields.of(Locale("es", "ES"))
        return today.get(weekFields.weekOfWeekBasedYear())
    }

    override fun getLastWeek(): Int {
        val lastWeek: LocalDate = (overrideDate ?: LocalDate.now()).minusWeeks(1)
        val weekFields = WeekFields.of(Locale("es", "ES"))
        return lastWeek.get(weekFields.weekOfWeekBasedYear())
    }

    override fun getCurrentWeekDay(): Int {
        return (overrideDate ?: LocalDate.now()).dayOfWeek.value
    }

    override fun isEvenCurrentWeek(): Boolean {
        return getCurrentWeek() % 2 == 0
    }

    override fun getTwoWeeksAgo(): Int {
        val twoWeeksAgo: LocalDate = (overrideDate ?: LocalDate.now()).minusWeeks(2)
        val weekFields = WeekFields.of(Locale("es", "ES"))
        return twoWeeksAgo.get(weekFields.weekOfWeekBasedYear())
    }
}