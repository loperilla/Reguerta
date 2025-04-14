package com.reguerta.domain.model.mapper

import com.reguerta.data.firebase.firestore.order.OrderModel
import com.reguerta.data.firebase.firestore.orderlines.OrderLineModel
import com.reguerta.domain.model.Order
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.OrderLineReceived

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model.mapper
 * Created By Manuel Lopera on 29/3/24 at 10:51
 * All rights reserved 2024
 */

fun OrderModel.toDto() = Order(
    id = orderId.orEmpty(),
    name = name.orEmpty(),
    surname = surname.orEmpty()
)

fun OrderLineModel.toReceived(product: Product, order: Order) = OrderLineReceived(
    orderName = order.name,
    orderSurname = order.surname,
    product = product,
    quantity = quantity ?: 0,
    subtotal = (quantity ?: 0).toDouble() * product.price.toDouble(),
    companyName = companyName.orEmpty()
)
