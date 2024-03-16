package com.reguerta.data.firebase.firestore.order

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.order
 * Created By Manuel Lopera on 13/3/24 at 19:10
 * All rights reserved 2024
 */
interface OrderServices {
    suspend fun getOrderByUserId(): Result<OrderModel>
}