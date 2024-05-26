package com.reguerta.domain.usecase.orderline

import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
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
    private val orderLineService: OrderLineService
) {
    suspend operator fun invoke(orderId: String): Flow<List<OrderLineProduct>> =
        orderLineService.getOrderLines(orderId).map {
            it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
        }
}
