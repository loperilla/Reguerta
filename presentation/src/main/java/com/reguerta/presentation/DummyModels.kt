package com.reguerta.presentation

import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 16/3/24 at 10:09
 * All rights reserved 2024
 */

val ALCAZAR = CommonProduct(
    name = "Alcazar",
    description = "Original de Jaén",
    container = "paquete",
    price = 2.5f,
    available = true,
    companyName = "Lopera SL",
    imageUrl = "",
    stock = 15,
    quantityContainer = 6,
    quantityWeight = 33,
    unity = "centilitro"
)

val ALCAZAR_WITH_ORDER = ProductWithOrderLine(
    commonProduct = ALCAZAR,
    orderLine = OrderLineProduct(
        orderId = "123",
        userId = "123",
        productId = "123",
        quantity = 1,
        week = 1
    )
)