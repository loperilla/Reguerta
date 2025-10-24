package com.reguerta.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.OrderLineReceived
import timber.log.Timber
import com.reguerta.domain.enums.CriticalTable
import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.enums.afterDays
import com.reguerta.domain.enums.toWeekDay
import java.time.DayOfWeek

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

fun getActiveCriticalTables(
    isProducer: Boolean,
    currentDay: DayOfWeek,
    deliveryDay: WeekDay
): List<CriticalTable> {
    val result = mutableListOf(CriticalTable.USERS, CriticalTable.PRODUCTS, CriticalTable.CONTAINERS, CriticalTable.MEASURES)
    val todayWD = currentDay.toWeekDay()
    if (!deliveryDay.afterDays().contains(todayWD)) {
        result += CriticalTable.ORDERS
    }
    Timber.tag("SYNC_CriticalTables").d(
        "isProducer=%s, currentDay=%s, deliveryDay=%s → CriticalTables=%s",
        isProducer, currentDay, deliveryDay, result
    )
    return result
}


@Composable
fun getWidthDevice(): Dp {
    val widthDp = LocalConfiguration.current.screenWidthDp
    Timber.tag("UI_ScreenWidth").d("screenWidthDp=%d", widthDp)
    return widthDp.dp
}

@Composable
fun getWidthDeviceDp(): Int {
    val widthDp = LocalConfiguration.current.screenWidthDp
    Timber.tag("UI_ScreenWidth").d("screenWidthDp=%d", widthDp)
    return widthDp
}

@Composable
fun ratio(): Float = getWidthDeviceDp().toFloat() / 400.toFloat()
@Composable
fun Int.resize(): Dp = (this * ratio()).dp
