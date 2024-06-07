package com.reguerta.presentation

import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.received.OrderLineReceived


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

// Utils.kt

fun getQuantitySum(line: OrderLineReceived, containers: List<Container>, measures: List<Measure>): String {
    val container = getContainerByNameOrPlural(line.product.container, containers)
    val measure = getMeasureByNameOrPlural(line.product.unity, measures)

    return if (container != null && measure != null) {
        if (container.name == "A granel") {
            val sum = line.quantity * line.product.quantityWeight
            "$sum ${measure.abbreviation}"
        } else {
            if (line.quantity > 1) container.plural else container.name
        }
    } else {
        ""
    }
}
