package com.reguerta.domain.enums


enum class TypeProducerUser(val value: String) {
    REGULAR("normal"),
    ODD("impar"),
    EVEN("par"),
    SHOP("compras");

    fun hasCommitmentThisWeek(isCurrentWeekEven: Boolean): Boolean {
        return when (this) {
            REGULAR, SHOP -> true
            ODD -> !isCurrentWeekEven
            EVEN -> isCurrentWeekEven
        }
    }
}