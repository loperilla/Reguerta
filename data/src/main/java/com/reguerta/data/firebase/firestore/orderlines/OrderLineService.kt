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
    suspend fun addOrderLineInDatabase(orderId: String, productId: String, productCompany: String)
    suspend fun updateQuantity(orderId: String, productId: String, quantity: Int)
    suspend fun deleteOrderLine(orderId: String, productId: String)
    suspend fun addOrderLineInFirebase(listToPush: List<OrderLineDTO>): Result<Unit>
    suspend fun getOrdersByCompanyAndWeek(): Flow<Result<List<OrderLineModel>>>
    suspend fun getOrdersByOrderId(orderId: String): Flow<Result<List<OrderLineModel>>>
}