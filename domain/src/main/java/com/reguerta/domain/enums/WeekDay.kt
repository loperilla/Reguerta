package com.reguerta.domain.enums

import java.time.DayOfWeek

enum class WeekDay {
    MON, TUE, WED, THU, FRI, SAT, SUN;

    companion object {
        val all = entries
    }
}

fun parseDeliveryDay(value: String?): WeekDay =
    runCatching { WeekDay.valueOf(value ?: "WED") }.getOrDefault(WeekDay.WED)

fun WeekDay.nextDay(): WeekDay =
    WeekDay.all[(this.ordinal + 1) % WeekDay.all.size]

fun WeekDay.plusDays(days: Int): WeekDay =
    WeekDay.all[(this.ordinal + days) % WeekDay.all.size]

fun WeekDay.toJavaDayOfWeek(): DayOfWeek =
    when (this) {
        com.reguerta.domain.enums.WeekDay.MON -> DayOfWeek.MONDAY
        com.reguerta.domain.enums.WeekDay.TUE -> DayOfWeek.TUESDAY
        com.reguerta.domain.enums.WeekDay.WED -> DayOfWeek.WEDNESDAY
        com.reguerta.domain.enums.WeekDay.THU -> DayOfWeek.THURSDAY
        com.reguerta.domain.enums.WeekDay.FRI -> DayOfWeek.FRIDAY
        com.reguerta.domain.enums.WeekDay.SAT -> DayOfWeek.SATURDAY
        com.reguerta.domain.enums.WeekDay.SUN -> DayOfWeek.SUNDAY
    }