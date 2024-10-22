package com.reguerta.domain.usecase.orderline

import android.util.Log
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.received.OrderLineReceived
import javax.inject.Inject

class MapOrderLinesWithProductsUseCase @Inject constructor() {
    operator fun invoke(orderLines: List<OrderLineReceived>, products: List<CommonProduct>): List<OrderLineReceived> {
        return orderLines.map { orderLine ->
            // aqui no uso el id como debería ser porque viene nulo en las orderlines * REVISAR
            // Con el nombre actualiza los mangos pero no lo hace bien con las ciruelas y melocotones
            val updatedProduct = products.find { it.id == orderLine.product.id } ?: orderLine.product
            Log.d("ORDERS", "updatedProduct: $updatedProduct")
            orderLine.copy(product = updatedProduct)
        }
    }
}
