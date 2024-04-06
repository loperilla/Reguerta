package com.reguerta.domain.model.new_order

import com.reguerta.data.firebase.firestore.order.OrderServices
import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.model.order.GetOrderByIdDataModel
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.mapper.toOrderLineDto
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
    private val productsService: ProductsService,
    private val orderServices: OrderServices,
    private val orderLineService: OrderLineService
) {
    private lateinit var orderId: String
    suspend fun checkIfExistOrderInFirebase(): Result<Boolean> {
        return when (val result = orderServices.getOrderByUserId()) {
            is GetOrderByIdDataModel.ExistInFirebase -> {
                orderId = result.order.orderId!!
                Result.success(true)
            }

            is GetOrderByIdDataModel.Failure -> Result.failure(Exception(result.error.toString()))
            is GetOrderByIdDataModel.NotExistInFirebase -> {
                orderId = result.orderModel.orderId!!
                Result.success(false)
            }
        }
    }

    suspend fun getOrderLines(): Flow<List<OrderLineProduct>> = orderLineService.getOrderLines(orderId).map {
        it.map { orderLineDTO -> orderLineDTO.toOrderLine() }
    }

    suspend fun deleteOrderLineLocal(productId: String) = orderLineService.deleteOrderLine(orderId, productId)

    suspend fun updateProductStock(productId: String, newQuantity: Int) =
        orderLineService.updateQuantity(orderId, productId, newQuantity)

    suspend fun addLocalOrderLine(
        productId: String, productCompany: String
    ) = orderLineService.addOrderLineInDatabase(orderId, productId, productCompany)

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
}
