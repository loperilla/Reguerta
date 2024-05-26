package com.reguerta.domain.model.received

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
    val companyName: String = ""
)

fun List<OrderLineReceived>.getAmount(): String {
    var amount = 0f
    forEach { amount += it.product.price * it.quantity }
    return String.format("%.2f", amount) + " €"
}

fun List<OrderLineReceived>.getDblAmount(): Double {
    var amount = 0.0
    forEach { amount += it.product.price * it.quantity }
    return amount
}

fun List<OrderLineReceived>.getQuantityByProduct(product: Product): Int {
    var quantity = 0
    forEach { if (it.product == product) quantity += it.quantity }
    return quantity
}

fun OrderLineReceived.fullOrderName() = "$orderName $orderSurname"