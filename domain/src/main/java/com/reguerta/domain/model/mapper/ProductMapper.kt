package com.reguerta.domain.model.mapper

import com.reguerta.data.firebase.firestore.orderlines.OrderLineDTO
import com.reguerta.data.firebase.firestore.products.ProductDTOModel
import com.reguerta.data.firebase.firestore.products.ProductModel
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.UnitType
import com.reguerta.domain.model.interfaces.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model.mapper
 * Created By Manuel Lopera on 15/3/24 at 19:53
 * All rights reserved 2024
 */

fun Product.priceFormatted(): String = String.format("%.2f", price) + "€"

fun Product.getUnitType(): UnitType = if (quantityContainer > 1) UnitType.PACK else UnitType.UNIT

fun Product.containerUnity(): String {
    val quantityContainerString = if (quantityContainer != 1) "$quantityContainer " else ""
    return "$quantityContainerString$container $quantityWeight $unity"
}

//fun Product.containerUnity(): String = "$quantityContainer $container $quantityWeight $unity"

fun Product.toDto(): ProductDTOModel = ProductDTOModel(
    container = container,
    description = description,
    name = name,
    price = price,
    available = available,
    companyName = companyName,
    urlImage = imageUrl,
    stock = stock,
    quantityContainer = quantityContainer,
    quantityWeight = quantityWeight,
    unity = unity
)

fun ProductModel.toDomain(): CommonProduct = CommonProduct(
    id = id.orEmpty(),
    container = container.orEmpty(),
    description = description.orEmpty(),
    name = name.orEmpty(),
    price = price ?: 0.0f,
    available = available ?: false,
    companyName = companyName.orEmpty(),
    imageUrl = urlImage.orEmpty(),
    stock = stock ?: 0,
    quantityContainer = quantityContainer ?: 0,
    quantityWeight = quantityWeight ?: 0,
    unity = unity.orEmpty()
)

fun ProductModel.toDomain(
    measure: Measure
): CommonProduct = CommonProduct(
    id = id.orEmpty(),
    container = container.orEmpty(),
    description = description.orEmpty(),
    name = name.orEmpty(),
    price = price ?: 0.0f,
    available = available ?: false,
    companyName = companyName.orEmpty(),
    imageUrl = urlImage.orEmpty(),
    stock = stock ?: 0,
    quantityContainer = quantityContainer ?: 0,
    quantityWeight = quantityWeight ?: 0,
    unity = unity.orEmpty()
)

fun List<ProductWithOrderLine>.getAmount() = this.sumOf { it.getAmount() }

fun ProductWithOrderLine.toOrderLineDto() = OrderLineDTO(
    orderId = orderLine.orderId,
    userId = orderLine.userId,
    productId = id,
    quantity = quantity,
    week = orderLine.week,
    companyName = companyName,
    subtotal = getAmount()
)