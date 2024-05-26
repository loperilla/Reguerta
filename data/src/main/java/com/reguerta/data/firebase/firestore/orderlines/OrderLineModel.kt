package com.reguerta.data.firebase.firestore.orderlines

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 29/3/24 at 10:43
 * All rights reserved 2024
 */

data class OrderLineModel(
    var id: String? = null,
    val orderId: String? = null,
    val userId: String? = null,
    val companyName: String? = null,
    val productId: String? = null,
    val quantity: Int? = null,
    val week: Int? = null
)
