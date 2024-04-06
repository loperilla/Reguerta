package com.reguerta.domain.model.new_order

import com.reguerta.data.firebase.firestore.order.OrderServices
import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.model.order.GetOrderByIdDataModel
import com.reguerta.domain.model.Order
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.domain.model.mapper.toDto
import com.reguerta.domain.model.mapper.toOrderLineDto
import com.reguerta.domain.model.mapper.toReceived
import com.reguerta.domain.model.received.OrderLineReceived
import com.reguerta.domain.model.toOrderLine
import kotlinx.coroutines.flow.Flow
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
    private val orderService: OrderServices,
    private val orderLineService: OrderLineService
) {
    private lateinit var order: Order
    suspend fun checkIfExistOrderInFirebase(): Result<Boolean> {
        return when (val result = orderService.getOrderByUserId()) {
            is GetOrderByIdDataModel.ExistInFirebase -> {
                order = result.order.toDto()
                Result.success(true)
            }

            is GetOrderByIdDataModel.Failure -> Result.failure(Exception(result.error.toString()))
            is GetOrderByIdDataModel.NotExistInFirebase -> {
                order = result.orderModel.toDto()
                Result.success(false)
            }
        }
    }

    suspend fun getOrderLines(): Flow<List<OrderLineProduct>> = orderLineService.getOrderLines(order.id).map {
        it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
    }

    suspend fun deleteOrderLineLocal(productId: String) = orderLineService.deleteOrderLine(order.id, productId)

    suspend fun updateProductStock(productId: String, newQuantity: Int) =
        orderLineService.updateQuantity(order.id, productId, newQuantity)

    suspend fun addLocalOrderLine(
        productId: String, productCompany: String
    ) = orderLineService.addOrderLineInDatabase(order.id, productId, productCompany)

    suspend fun pushOrderLinesToFirebase(listToPush: List<ProductWithOrderLine>): Result<Unit> {
        val dtoList = listToPush.map {
            it.toOrderLineDto()
        }
        return orderLineService.addOrderLineInFirebase(dtoList).fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    suspend fun getOrderLinesFromCurrentWeek(): Flow<List<OrderLineReceived>> =
        orderLineService.getOrdersByOrderId(order.id).map {
            it.fold(
                onSuccess = { orderLines ->
                    val listReturn = mutableListOf<OrderLineReceived>()
                    orderLines.forEach { model ->
                        val product = productService.getProductById(model.productId.orEmpty()).getOrThrow().toDomain()
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
