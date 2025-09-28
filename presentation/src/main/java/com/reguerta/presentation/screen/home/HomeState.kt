package com.reguerta.presentation.screen.home

import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.repository.ConfigCheckResult
import java.time.DayOfWeek

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:49
 * All rights reserved 2024
 */
data class HomeState(
    val goOut: Boolean = false,
    val showAreYouSure: Boolean = false,
    val showNotAuthorizedDialog: Boolean = false,
    val isCurrentUserAdmin: Boolean = false,
    val isCurrentUserProducer: Boolean = false,
    val currentDay: DayOfWeek = DayOfWeek.MONDAY,
    val deliveryDay: WeekDay = WeekDay.WED,
    val configCheckResult: ConfigCheckResult? = null,
    val isLoading: Boolean = false
)
