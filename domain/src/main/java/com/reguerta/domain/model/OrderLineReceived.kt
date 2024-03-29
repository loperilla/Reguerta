package com.reguerta.domain.model

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

fun OrderLineReceived.getAmount() = quantity * product.price