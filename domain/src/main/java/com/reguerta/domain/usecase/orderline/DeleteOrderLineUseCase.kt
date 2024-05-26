package com.reguerta.domain.usecase.orderline

import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 17/3/24 at 10:34
 * All rights reserved 2024
 */

class DeleteOrderLineUseCase @Inject constructor(
    private val orderLineService: OrderLineService
) {
    suspend operator fun invoke(orderId: String, productId: String) =
        orderLineService.deleteOrderLine(orderId, productId)
}