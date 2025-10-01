package com.reguerta.domain.usecase.config

import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.enums.parseDeliveryDay
import javax.inject.Inject

class GetDeliveryDayUseCase @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase
) {
    suspend operator fun invoke(): WeekDay {
        val config = getConfigUseCase()
        val deliveryDayValue = config.otherConfig["deliveryDayOfWeek"] as? String
        return parseDeliveryDay(deliveryDayValue)
    }
}