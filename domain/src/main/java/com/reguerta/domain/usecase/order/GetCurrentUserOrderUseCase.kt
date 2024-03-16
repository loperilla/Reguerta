package com.reguerta.domain.usecase.order

import com.reguerta.data.firebase.firestore.order.OrderServices
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.order
 * Created By Manuel Lopera on 13/3/24 at 19:35
 * All rights reserved 2024
 */
class GetCurrentUserOrderUseCase @Inject constructor(
    private val orderServices: OrderServices
) {

    suspend operator fun invoke(): Result<String> {
        return orderServices.getOrderByUserId().fold(
            onSuccess = { Result.success(it.orderId!!) },
            onFailure = { Result.failure(it) }
        )
    }
}