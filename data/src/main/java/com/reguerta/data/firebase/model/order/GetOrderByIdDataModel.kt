package com.reguerta.data.firebase.model.order

import com.reguerta.data.firebase.firestore.order.OrderModel
import com.reguerta.data.firebase.model.DataError

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.model.order
 * Created By Manuel Lopera on 6/4/24 at 11:51
 * All rights reserved 2024
 */
sealed class GetOrderByIdDataModel {

    data class ExistInFirebase(val order: OrderModel) : GetOrderByIdDataModel()

    data class NotExistInFirebase(val orderModel: OrderModel) : GetOrderByIdDataModel()

    data class Failure(val error: DataError) : GetOrderByIdDataModel()
}
