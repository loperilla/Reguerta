package com.reguerta.domain.usecase.orderline

import com.reguerta.data.firebase.firestore.order.OrderServices
import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.model.OrderLineReceived
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.domain.model.mapper.toDto
import com.reguerta.domain.model.mapper.toReceived
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.orderline
 * Created By Manuel Lopera on 29/3/24 at 10:46
 * All rights reserved 2024
 */
class OrderReceivedModel @Inject constructor(
    private val orderLineService: OrderLineService,
    private val productService: ProductsService,
    private val orderService: OrderServices
) {

    suspend operator fun invoke(): Flow<List<OrderLineReceived>> =
        orderLineService.getOrdersByCompanyAndWeek().map {
            it.fold(
                onSuccess = { orderLines ->
                    val listReturn = mutableListOf<OrderLineReceived>()
                    orderLines.forEach { model ->
                        val product = productService.getProductById(model.productId.orEmpty()).getOrThrow().toDomain()
                        val order = orderService.getOrderByUserId(model.userId.orEmpty()).getOrThrow().toDto()
                        listReturn.add(model.toReceived(product, order))
                    }
                    listReturn
                },
                onFailure = {
                    emptyList()
                }
            )
        }
}
