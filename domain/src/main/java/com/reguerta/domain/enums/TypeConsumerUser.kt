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

    /**
     * Regla de restricción solicitada:
     * Solo está PERMITIDO (y, en la práctica, requerido) aceptar/renunciar
     * al compromiso cuando esta función devuelve true.
     * Si devuelve false, debes bloquear COMMIT/RESIGN en el carrito.
     */
    fun isCommitmentAllowedThisWeek(isCurrentWeekEven: Boolean): Boolean {
        return hasCommitmentThisWeek(isCurrentWeekEven)
    }
}
