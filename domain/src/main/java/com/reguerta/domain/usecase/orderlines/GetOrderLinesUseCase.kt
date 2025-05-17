package com.reguerta.domain.usecase.orderlines

import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.toOrderLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 15/3/24 at 19:22
 * All rights reserved 2024
 */

class GetOrderLinesUseCase @Inject constructor(
    private val orderLinesService: OrderLinesService
) {
    suspend operator fun invoke(orderId: String): Flow<List<OrderLineProduct>> =
        orderLinesService.getOrderLines(orderId).map {
            it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
        }
}
