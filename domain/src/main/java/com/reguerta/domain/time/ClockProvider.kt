package com.reguerta.domain.time

import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.ZoneId

interface ClockProvider {
    fun today(): LocalDate
    fun now(): ZonedDateTime
    val zoneId: ZoneId get() = ZoneId.systemDefault()
}