package com.reguerta.domain.usecase.orderline

import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 16/3/24 at 11:09
 * All rights reserved 2024
 */

class UpdateQuantityOrderLineUseCase @Inject constructor(
    private val orderLineService: OrderLineService
) {
    suspend operator fun invoke(orderId: String, productId: String, quantity: Int) =
        orderLineService.updateQuantity(orderId, productId, quantity)
}