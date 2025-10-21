package com.reguerta.presentation

import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.OrderLineReceived
import timber.log.Timber
import com.google.firebase.Timestamp
import com.reguerta.domain.enums.CriticalTable
import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.enums.afterDays
import com.reguerta.domain.enums.toWeekDay
import java.time.DayOfWeek

// NOTE: Legacy text sizing (ResizedTextSizes/ProvideTextSizes) removed.
// Use MaterialTheme.typography (ReguertaTypography) for text roles
// and Dimens for spacing. See docs/design-system.

fun checkAllStringAreNotEmpty(vararg inputValues: String) = inputValues.all { it.isNotEmpty() }

fun getContainerSingularForm(currentType: String, items: List<Container>): String {
    return items.find { it.plural == currentType }?.name ?: currentType
}

fun getMeasureSingularForm(currentType: String, items: List<Measure>): String {
    return items.find { it.plural == currentType }?.name ?: currentType
}

fun getContainerPluralForm(currentType: String, items: List<Container>): String {
    return items.find { it.name == currentType }?.plural ?: currentType
}

fun getMeasurePluralForm(currentType: String, items: List<Measure>): String {
    return items.find { it.name == currentType }?.plural ?: currentType
}

fun getContainerByNameOrPlural(str: String, items: List<Container>): Container? {
    return items.find { it.name == str || it.plural == str }
}

fun getMeasureByNameOrPlural(str: String, items: List<Measure>): Measure? {
    return items.find { it.name == str || it.plural == str }
}

fun getQuantitySum(line: OrderLineReceived, containers: List<Container>, measures: List<Measure>): String {
    val container = getContainerByNameOrPlural(line.product.container, containers)
    val measure = getMeasureByNameOrPlural(line.product.unity, measures)
    val sum = line.quantity * line.product.quantityWeight
    return when (container?.name) {
        ContainerType.BULK.value -> { "$sum ${measure?.abbreviation ?: ""}" }
        ContainerType.COMMIT.value -> { ContainerType.COMMIT.nameAbr() }
        ContainerType.COMMIT_MANGOES.value -> { measure?.abbreviation ?: "" }
        ContainerType.COMMIT_AVOCADOS.value -> { measure?.abbreviation ?: "" }
        else -> { if (line.quantity > 1) container?.plural ?: "" else container?.name ?: "" }
    }
}

fun isVersionGreater(version1: String, version2: String): Boolean {
    val parts1 = version1.split(".").map { it.toIntOrNull() ?: 0 }
    val parts2 = version2.split(".").map { it.toIntOrNull() ?: 0 }

    val maxLength = maxOf(parts1.size, parts2.size)
    val padded1 = parts1 + List(maxLength - parts1.size) { 0 }
    val padded2 = parts2 + List(maxLength - parts2.size) { 0 }

    for (i in 0 until maxLength) {
        if (padded1[i] > padded2[i]) return true
        if (padded1[i] < padded2[i]) return false
    }

    return false
}

fun getTablesToSync(
    remoteTimestamps: Map<String, Timestamp>,
    localTimestamps: Map<String, Timestamp>
): List<String> {
    return remoteTimestamps.filter { (key, remoteValue) ->
        val localValue = localTimestamps[key]
        localValue == null || remoteValue.toDate().after(localValue.toDate())
    }.map { it.key }.also {
    Timber.tag("SyncCheck").d("Tables to sync: $it")
    }
}


fun getActiveCriticalTables(
    isAdmin: Boolean,
    isProducer: Boolean,
    currentDay: DayOfWeek,
    deliveryDay: WeekDay
): List<CriticalTable> {
    val result = mutableListOf(CriticalTable.PRODUCTS, CriticalTable.CONTAINERS, CriticalTable.MEASURES)
    if (isAdmin) result += CriticalTable.USERS

    val todayWD = currentDay.toWeekDay()
    if (!deliveryDay.afterDays().contains(todayWD)) {
        result += CriticalTable.ORDERS
    }
    Timber.tag("SYNC_CriticalTables").d(
        "isAdmin=%s, isProducer=%s, currentDay=%s, deliveryDay=%s → CriticalTables=%s",
        isAdmin, isProducer, currentDay, deliveryDay, result
    )
    return result
}
