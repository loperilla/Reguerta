package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.orderlines.OrderLineDTO

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 15/3/24 at 18:59
 * All rights reserved 2024
 */
data class OrderLineProduct(
    val orderId: String,
    val userId: String,
    val productId: String,
    val companyName: String,
    val quantity: Int,
    val week: Int
)

fun OrderLineDTO.toOrderLine() = OrderLineProduct(
    orderId = orderId,
    userId = userId,
    productId = productId,
    companyName = companyName,
    quantity = quantity,
    week = week
)