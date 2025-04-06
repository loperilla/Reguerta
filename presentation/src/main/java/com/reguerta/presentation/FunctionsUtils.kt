package com.reguerta.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.received.OrderLineReceived
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

@Composable
fun Int.resize(): TextUnit {
    val widthDevice = LocalConfiguration.current.screenWidthDp
    Timber.tag("DeviceWidth").d("Current device width: %s", widthDevice)
    return when {
        widthDevice < 600 -> (this * (widthDevice / 375f)).sp
        widthDevice < 800 -> (this * 1.5).sp
        widthDevice < 1000 -> (this * 1.6).sp
        else -> (this * 1.9).sp
    }
}

@Composable
fun getResizedTextSize(baseSize: Int): TextUnit {
    return baseSize.resize()
}

@Composable
fun ResizedTextSizes() {
    val extraSmall = getResizedTextSize(TEXT_EXTRA_SMALL)
    val small = getResizedTextSize(TEXT_SMALL)
    val medium = getResizedTextSize(TEXT_MEDIUM)
    val large = getResizedTextSize(TEXT_LARGE)
    val extraLarge = getResizedTextSize(TEXT_EXTRA_LARGE)
    val topBar = getResizedTextSize(TEXT_TOP)
    val special = getResizedTextSize(TEXT_PLUS)

    val pairBtn = getResizedTextSize(TEXT_PAIR_BTN)
    val singleBtn = getResizedTextSize(TEXT_SINGLE_BTN)
    val specialBtn = getResizedTextSize(TEXT_SPECIAL_BTN)

    val dlgBody = getResizedTextSize(TEXT_DLG_BODY)
    val dlgTitle = getResizedTextSize(TEXT_DLG_TITLE)
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

