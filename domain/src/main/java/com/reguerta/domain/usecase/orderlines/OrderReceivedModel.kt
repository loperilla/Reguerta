package com.reguerta.domain.usecase.orderlines

import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.model.DataResult
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.domain.model.mapper.toDto
import com.reguerta.domain.model.mapper.toReceived
import com.reguerta.domain.model.OrderLineReceived
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
    private val orderLinesService: OrderLinesService,
    private val productService: ProductsService,
    private val orderService: OrdersService
) {
    suspend operator fun invoke(): Flow<List<OrderLineReceived>> =
        orderLinesService.getOrdersByCompanyAndWeek().map {
            it.fold(
                onSuccess = { orderLines ->
                    val listReturn = mutableListOf<OrderLineReceived>()
                    orderLines.forEach { model ->
                        val productId = model.productId
                        if (productId.isNullOrEmpty()) {
                            return@forEach // O manejar según corresponda
                        }
                        val productResult = productService.getProductById(productId)
                        if (productResult.isFailure) {
                            return@forEach
                        }
                        val product = productResult.getOrThrow().toDomain()
                        val order = when (val result = orderService.getOrderByUserId(model.userId.orEmpty())) {
                            is DataResult.Error -> { null }
                            is DataResult.Success -> result.data.toDto()
                        }
                        if (order != null) { listReturn.add(model.toReceived(product, order)) }
                    }
                    listReturn
                },
                onFailure = {
                    emptyList()
                }
            )
        }
    }
