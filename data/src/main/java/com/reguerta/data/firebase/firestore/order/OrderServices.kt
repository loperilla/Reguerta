package com.reguerta.data.firebase.firestore.order

import com.reguerta.data.firebase.model.DataError
import com.reguerta.data.firebase.model.DataResult
import com.reguerta.data.firebase.model.order.GetOrderByIdDataModel

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.order
 * Created By Manuel Lopera on 13/3/24 at 19:10
 * All rights reserved 2024
 */
interface OrderServices {
    suspend fun getOrderByUserId(): GetOrderByIdDataModel
    suspend fun getOrderByUserId(userId: String): DataResult<OrderModel, DataError.Firebase>
}