package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.model.DataResult
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.domain.model.mapper.toDto
import com.reguerta.domain.model.mapper.toOrderLineDto
import com.reguerta.domain.model.mapper.toReceived
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model.new_order
 * Created By Manuel Lopera on 3/4/24 at 19:53
 * All rights reserved 2024
 */

class NewOrderModel @Inject constructor(
    private val productService: ProductsService,
    private val orderService: OrdersService,
    private val orderLinesService: OrderLinesService
) {
    private var order: Order? = null

    suspend fun checkIfExistOrderInFirebase(): Result<Boolean> {
        return when (val result = orderService.getOrderByUserId()) {
            is DataResult.Success -> {
                val currentOrder = result.data.toDto()
                order = currentOrder
                checkIfHasFirebaseOrderLines(currentOrder.id)
            }

            is DataResult.Error -> {
                order = null
                Result.failure(IllegalStateException("No se pudo obtener el pedido actual en Firebase"))
            }
        }
    }

    suspend fun checkIfExistLastWeekOrderInFirebase(): Result<Boolean> {
        return when (val result = orderService.getLastOrderByUserId()) {
            is DataResult.Error -> {
                order = null
                Result.failure(Exception("Order not found in firebase"))
            }
            is DataResult.Success -> {
                val lastWeekOrder = result.data.toDto()
                order = lastWeekOrder
                checkIfHasFirebaseOrderLines(lastWeekOrder.id)
            }
        }
    }

    private suspend fun checkIfHasFirebaseOrderLines(orderId: String): Result<Boolean> =
        orderLinesService.checkIfExistOrderInFirebase(orderId)

    suspend fun getOrderLines(): Flow<List<OrderLineProduct>> {
        val currentOrder = order ?: return flowOf(emptyList())
        return orderLinesService.getOrderLines(currentOrder.id).map {
            it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
        }
    }

    suspend fun deleteOrderLineLocal(productId: String) =
        order?.let { currentOrder ->
            orderLinesService.deleteOrderLine(currentOrder.id, productId)
        }

    suspend fun updateProductStock(productId: String, newQuantity: Int) =
        order?.let { currentOrder ->
            orderLinesService.updateQuantity(currentOrder.id, productId, newQuantity)
        }

    suspend fun addLocalOrderLine(productId: String, productCompany: String) {
        order?.let { currentOrder ->
            orderLinesService.addOrderLineInDatabase(currentOrder.id, productId, productCompany)
        }
    }

    suspend fun pushOrderLinesToFirebase(listToPush: List<ProductWithOrderLine>): Result<Unit> {
        val currentOrder = order ?: return Result.failure(
            IllegalStateException("No hay pedido activo para subir líneas")
        )
        val dtoList = listToPush.map {
            it.toOrderLineDto().copy(orderId = currentOrder.id)
        }
        return orderLinesService.addOrderLineInFirebase(dtoList).fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    suspend fun getOrderLinesFromCurrentWeek(): Flow<List<OrderLineReceived>> {
        val currentOrder = order ?: return flowOf(emptyList())
        return orderLinesService.getOrdersByOrderId(currentOrder.id).map { result ->
            val listReturn = mutableListOf<OrderLineReceived>()
            result.onSuccess { orderLines ->
                orderLines.forEach { model ->
                    val product =
                        productService.getProductById(model.productId.orEmpty()).getOrThrow()
                            .toDomain()
                    listReturn.add(model.toReceived(product, currentOrder))
                }
            }
            listReturn
        }
    }

    suspend fun deleteOrder(): Result<Unit> {
        val currentOrder = order ?: return Result.failure(
            IllegalStateException("No hay pedido activo para borrar")
        )

        return orderLinesService.deleteFirebaseOrderLine(currentOrder.id).fold(
            onSuccess = {
                when (orderService.deleteOrder(currentOrder.id)) {
                    is DataResult.Success -> {
                        order = null
                        Result.success(Unit)
                    }
                    is DataResult.Error -> Result.failure(
                        IllegalStateException("No se pudo borrar el pedido en Firebase")
                    )
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun getOrderLinesList(): List<OrderLineProduct> = getOrderLines().first()
}
