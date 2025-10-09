package com.reguerta.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.OrderLineReceived
import com.reguerta.presentation.ui.TEXT_DLG_BODY
import com.reguerta.presentation.ui.TEXT_DLG_TITLE
import com.reguerta.presentation.ui.TEXT_EXTRA_LARGE
import com.reguerta.presentation.ui.TEXT_EXTRA_SMALL
import com.reguerta.presentation.ui.TEXT_LARGE
import com.reguerta.presentation.ui.TEXT_MEDIUM
import com.reguerta.presentation.ui.TEXT_PAIR_BTN
import com.reguerta.presentation.ui.TEXT_PLUS
import com.reguerta.presentation.ui.TEXT_SINGLE_BTN
import com.reguerta.presentation.ui.TEXT_SMALL
import com.reguerta.presentation.ui.TEXT_SPECIAL_BTN
import com.reguerta.presentation.ui.TEXT_TOP
import timber.log.Timber
import com.google.firebase.Timestamp
import com.reguerta.domain.enums.CriticalTable
import com.reguerta.domain.enums.WeekDay
import java.time.DayOfWeek

@Composable
fun Int.resize(): TextUnit {
    val widthDevice = LocalConfiguration.current.screenWidthDp.toFloat()
    Timber.tag("DeviceWidth").d("Current device width: %s", widthDevice)
    return when {
        widthDevice < 600f -> (this * (widthDevice / 375f)).sp
        widthDevice < 800f -> (this * 1.5).sp
        widthDevice < 1000f -> (this * 1.6).sp
        else -> (this * 1.9).sp
    }
}

@Composable
fun getResizedTextSize(baseSize: Int): TextUnit {
    return baseSize.resize()
}

@Composable
fun ResizedTextSizes(): TextSizes = rememberTextSizes()

// --- Centralización tipográfica basada en tamaño de dispositivo ---
data class TextSizes(
    val extraSmall: TextUnit,
    val small: TextUnit,
    val medium: TextUnit,
    val large: TextUnit,
    val extraLarge: TextUnit,
    val topBar: TextUnit,
    val special: TextUnit,
    val pairBtn: TextUnit,
    val singleBtn: TextUnit,
    val specialBtn: TextUnit,
    val dlgBody: TextUnit,
    val dlgTitle: TextUnit
)

// Fallback por si no se provee explícitamente (usa tamaños base sin escalar)
val LocalTextSizes = staticCompositionLocalOf {
    TextSizes(
        extraSmall = TEXT_EXTRA_SMALL.sp,
        small = TEXT_SMALL.sp,
        medium = TEXT_MEDIUM.sp,
        large = TEXT_LARGE.sp,
        extraLarge = TEXT_EXTRA_LARGE.sp,
        topBar = TEXT_TOP.sp,
        special = TEXT_PLUS.sp,
        pairBtn = TEXT_PAIR_BTN.sp,
        singleBtn = TEXT_SINGLE_BTN.sp,
        specialBtn = TEXT_SPECIAL_BTN.sp,
        dlgBody = TEXT_DLG_BODY.sp,
        dlgTitle = TEXT_DLG_TITLE.sp
    )
}

/**
 * Calcula los tamaños redimensionados para el dispositivo actual.
 * Lee `LocalConfiguration` para volver a componer cuando cambie la anchura.
 */
@Composable
fun rememberTextSizes(): TextSizes {
    // Tocar configuración para que cambie con la anchura; no necesitamos el valor
    val currentConfig = LocalConfiguration.current
    return TextSizes(
        extraSmall = getResizedTextSize(TEXT_EXTRA_SMALL),
        small = getResizedTextSize(TEXT_SMALL),
        medium = getResizedTextSize(TEXT_MEDIUM),
        large = getResizedTextSize(TEXT_LARGE),
        extraLarge = getResizedTextSize(TEXT_EXTRA_LARGE),
        topBar = getResizedTextSize(TEXT_TOP),
        special = getResizedTextSize(TEXT_PLUS),
        pairBtn = getResizedTextSize(TEXT_PAIR_BTN),
        singleBtn = getResizedTextSize(TEXT_SINGLE_BTN),
        specialBtn = getResizedTextSize(TEXT_SPECIAL_BTN),
        dlgBody = getResizedTextSize(TEXT_DLG_BODY),
        dlgTitle = getResizedTextSize(TEXT_DLG_TITLE)
    )
}

/**
 * Proveedor de tamaños tipográficos para toda la jerarquía Compose.
 * Envuelve tu `NavHost` o `Scaffold` raíz con esto.
 */
@Composable
fun ProvideTextSizes(content: @Composable () -> Unit) {
    val sizes = rememberTextSizes()
    CompositionLocalProvider(LocalTextSizes provides sizes) {
        content()
    }
}

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

fun DayOfWeek.toWeekDay(): WeekDay {
    return when (this) {
        DayOfWeek.MONDAY -> WeekDay.MON
        DayOfWeek.TUESDAY -> WeekDay.TUE
        DayOfWeek.WEDNESDAY -> WeekDay.WED
        DayOfWeek.THURSDAY -> WeekDay.THU
        DayOfWeek.FRIDAY -> WeekDay.FRI
        DayOfWeek.SATURDAY -> WeekDay.SAT
        DayOfWeek.SUNDAY -> WeekDay.SUN
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

    val weekDay = currentDay.toWeekDay()
    if (isProducer && weekDay.ordinal in WeekDay.MON.ordinal..deliveryDay.ordinal) {
        result += CriticalTable.ORDERS
    }
    Timber.tag("SYNC_CriticalTables").d(
        "isAdmin=%s, isProducer=%s, currentDay=%s, deliveryDay=%s → CriticalTables=%s",
        isAdmin, isProducer, currentDay, deliveryDay, result
    )
    return result
}
