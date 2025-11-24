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
    lateinit var order: Order

    suspend fun checkIfExistOrderInFirebase(): Result<Boolean> {
        return when (val result = orderService.getOrderByUserId()) {
            is DataResult.Success -> {
                // Tenemos un order en Firebase para este usuario/semana
                order = result.data.toDto()
                // Delegamos en orderLinesService para saber si tiene líneas
                checkIfHasFirebaseOrderLines(order.id)
            }

            is DataResult.Error -> {
                // Desde el punto de vista de la lógica de dominio, esto
                // para ti significa "no hay pedido actual" → false.
                // Si en el futuro quisieras distinguir error real de "no encontrado",
                // aquí es donde habría que afinar usando info extra del DataResult.Error.
                Result.success(false)
            }
        }
    }
    suspend fun checkIfExistLastWeekOrderInFirebase(): Result<Boolean> {
        return when (val result = orderService.getLastOrderByUserId()) {
            is DataResult.Error -> Result.failure(Exception("Order not found in firebase"))
            is DataResult.Success -> {
                order = result.data.toDto()
                checkIfHasFirebaseOrderLines(order.id)
            }
        }
    }

    private suspend fun checkIfHasFirebaseOrderLines(orderId: String): Result<Boolean> =
        orderLinesService.checkIfExistOrderInFirebase(orderId)

    suspend fun getOrderLines(): Flow<List<OrderLineProduct>> =
        orderLinesService.getOrderLines(order.id).map {
            it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
        }

    suspend fun deleteOrderLineLocal(productId: String) =
        orderLinesService.deleteOrderLine(order.id, productId)

    suspend fun updateProductStock(productId: String, newQuantity: Int) =
        orderLinesService.updateQuantity(order.id, productId, newQuantity)

    suspend fun addLocalOrderLine(productId: String, productCompany: String) {
        orderLinesService.addOrderLineInDatabase(order.id, productId, productCompany)
    }

    suspend fun pushOrderLinesToFirebase(listToPush: List<ProductWithOrderLine>): Result<Unit> {
        val dtoList = listToPush.map {
            it.toOrderLineDto()
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

    suspend fun getOrderLinesFromCurrentWeek(): Flow<List<OrderLineReceived>> =
        orderLinesService.getOrdersByOrderId(order.id).map { result ->
            val listReturn = mutableListOf<OrderLineReceived>()
            result.onSuccess { orderLines ->
                orderLines.forEach { model ->
                    val product =
                        productService.getProductById(model.productId.orEmpty()).getOrThrow()
                            .toDomain()
                    listReturn.add(model.toReceived(product, order))
                }
            }
            listReturn
        }

    suspend fun deleteOrder() {
        orderLinesService.deleteFirebaseOrderLine(order.id)
        orderService.deleteOrder(order.id)
    }

    suspend fun getOrderLinesList(): List<OrderLineProduct> {
        return getOrderLines().first()
    }

}
