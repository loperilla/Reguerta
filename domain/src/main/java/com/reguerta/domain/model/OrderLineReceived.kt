package com.reguerta.domain.model

import android.annotation.SuppressLint
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.interfaces.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 29/3/24 at 10:56
 * All rights reserved 2024
 */

data class OrderLineReceived(
    val orderName: String,
    val orderSurname: String,
    val product: Product,
    val quantity: Int,
    val subtotal: Double,
    val companyName: String = ""
)

@SuppressLint("DefaultLocale")
fun List<OrderLineReceived>.getAmount(): String {
    var amount = 0.0
    forEach {
        amount += it.product.price * it.quantity
    }
    return String.format("%.2f", amount) + " €"
}

fun List<OrderLineReceived>.getDblAmount(): Double {
    var amount = 0.0
    forEach {
        val subtotal = if (it.product.container == ContainerType.COMMIT_MANGOES.value
                        || it.product.container == ContainerType.COMMIT_AVOCADOS.value) {
            it.product.price
        } else {
            it.product.price * it.quantity
        }
        amount += subtotal
    }
    return amount
}

fun List<OrderLineReceived>.getQuantityByProduct(product: Product): Int {
    var quantity = 0
    forEach { if (it.product == product) quantity += it.quantity }
    return quantity
}

fun OrderLineReceived.fullOrderName() = "$orderName $orderSurname"