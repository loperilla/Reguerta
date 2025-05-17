package com.reguerta.domain.usecase.orderlines

import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 16/3/24 at 10:36
 * All rights reserved 2024
 */

class AddOrderLineUseCase @Inject constructor(
    private val orderLinesService: OrderLinesService
) {
    suspend operator fun invoke(orderId: String, productId: String, productCompany: String) =
        orderLinesService.addOrderLineInDatabase(orderId, productId, productCompany)
}