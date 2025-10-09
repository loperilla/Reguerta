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

fun WeekDay.plusDays(days: Int): WeekDay = {
    val size = WeekDay.all.size
    val shift = ((days % size) + size) % size
    WeekDay.all[(this.ordinal + shift) % size]
}()

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

// --- iOS parity: helpers de orden y rangos relativos ---
val WeekDay.orderValue: Int
    get() = this.ordinal + 1 // Lunes = 1 ... Domingo = 7, coincide con java.time

fun WeekDay.equalOrAfterDays(): List<WeekDay> =
    WeekDay.all.filter { it.ordinal >= this.ordinal }

fun WeekDay.equalOrBeforeDays(): List<WeekDay> =
    WeekDay.all.filter { it.ordinal <= this.ordinal }

fun WeekDay.afterDays(): List<WeekDay> =
    WeekDay.all.filter { it.ordinal > this.ordinal }

fun WeekDay.beforeDays(): List<WeekDay> =
    WeekDay.all.filter { it.ordinal < this.ordinal }

// --- Reserved day (mismo criterio que iOS): el día siguiente, salvo sábado/domingo -> null ---
fun reservedDayFor(deliveryDay: WeekDay): WeekDay? =
    when (deliveryDay) {
        WeekDay.MON -> WeekDay.TUE
        WeekDay.TUE -> WeekDay.WED
        WeekDay.WED -> WeekDay.THU
        WeekDay.THU -> WeekDay.FRI
        WeekDay.FRI -> WeekDay.SAT
        WeekDay.SAT, WeekDay.SUN -> null
    }

fun WeekDay.isReservedDayFor(deliveryDay: WeekDay): Boolean =
    this == reservedDayFor(deliveryDay)