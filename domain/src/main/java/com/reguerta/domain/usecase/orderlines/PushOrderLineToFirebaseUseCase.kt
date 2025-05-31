package com.reguerta.domain.usecase.orderlines

import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.mapper.toOrderLineDto
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 17/3/24 at 12:58
 * All rights reserved 2024
 */

class PushOrderLineToFirebaseUseCase @Inject constructor(
    private val orderLinesService: OrderLinesService
) {
    suspend operator fun invoke(listToPush: List<ProductWithOrderLine>): Result<Unit> {
        val dtoList = listToPush.map {
            it.toOrderLineDto()
        }
        return orderLinesService.addOrderLineInFirebase(dtoList)
    }
}