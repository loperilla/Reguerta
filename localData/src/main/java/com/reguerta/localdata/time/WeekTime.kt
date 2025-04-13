package com.reguerta.localdata.time

import java.time.LocalDate

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.time
 * Created By Manuel Lopera on 13/3/24 at 18:25
 * All rights reserved 2024
 */

interface WeekTime {
    fun getCurrentWeek(): Int
    fun getLastWeek(): Int
    fun getCurrentWeekDay(): Int
    fun isEvenCurrentWeek(): Boolean
    fun getTwoWeeksAgo(): Int
    fun setTestDate(date: LocalDate)
    fun clearTestDate()
}
