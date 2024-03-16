package com.reguerta.data.firebase.firestore.orderlines

import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 15/3/24 at 19:11
 * All rights reserved 2024
 */
interface OrderLineService {
    suspend fun getOrderLines(orderId: String): Flow<List<OrderLineDTO>>
    suspend fun addOrderLine(orderId: String, productId: String)
    suspend fun updateQuantity(orderId: String, productId: String, quantity: Int)
}