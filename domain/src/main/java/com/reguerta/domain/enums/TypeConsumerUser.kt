package com.reguerta.domain.enums


enum class TypeConsumerUser(val value: String) {
    REGULAR("normal"),
    ODD("impar"),
    EVEN("par"),
    NONE("sin");

    fun hasCommitmentThisWeek(isCurrentWeekEven: Boolean): Boolean {
        return when (this) {
            REGULAR -> true
            ODD -> !isCurrentWeekEven
            EVEN -> isCurrentWeekEven
            NONE -> false
        }
    }
}
