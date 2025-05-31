package com.reguerta.domain.usecase.orderlines

import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 16/3/24 at 11:09
 * All rights reserved 2024
 */

class UpdateQuantityOrderLineUseCase @Inject constructor(
    private val orderLinesService: OrderLinesService
) {
    suspend operator fun invoke(orderId: String, productId: String, quantity: Int) =
        orderLinesService.updateQuantity(orderId, productId, quantity)
}