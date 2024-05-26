package com.reguerta.data.firebase.firestore.orderlines

import com.reguerta.localdata.database.entity.OrderLineEntity

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 15/3/24 at 19:12
 * All rights reserved 2024
 */

data class OrderLineDTO(
    val orderId: String,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val week: Int,
    val companyName: String = "",
    val subtotal: Double = 0.0
)

fun OrderLineEntity.toDTO() = OrderLineDTO(
    orderId = orderId,
    userId = userId,
    productId = productId,
    quantity = quantity,
    week = week
)