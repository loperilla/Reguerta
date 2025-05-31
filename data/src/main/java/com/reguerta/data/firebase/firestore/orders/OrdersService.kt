package com.reguerta.data.firebase.firestore.orders

import com.reguerta.data.firebase.model.DataError
import com.reguerta.data.firebase.model.DataResult

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orders
 * Created By Manuel Lopera on 13/3/24 at 19:10
 * All rights reserved 2024
 */

interface OrdersService {
    suspend fun getOrderByUserId(): DataResult<OrderModel, DataError.Firebase>
    suspend fun getLastOrderByUserId(): DataResult<OrderModel, DataError.Firebase>
    suspend fun getOrderByUserId(userId: String): DataResult<OrderModel, DataError.Firebase>
    suspend fun getLastOrderByUserId(userId: String): DataResult<OrderModel, DataError.Firebase>
    suspend fun deleteOrder(orderId: String): DataResult<Unit, DataError.Firebase>
    suspend fun getAllOrders(): Result<List<OrderModel>>
}