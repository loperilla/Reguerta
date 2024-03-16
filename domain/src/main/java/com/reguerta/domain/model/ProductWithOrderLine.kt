package com.reguerta.domain.model

import com.reguerta.domain.model.interfaces.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 15/3/24 at 19:45
 * All rights reserved 2024
 */
data class ProductWithOrderLine(
    private val commonProduct: CommonProduct,
    val orderLine: OrderLineProduct
) : Product {
    override val id: String
        get() = commonProduct.id
    override val container: String
        get() = commonProduct.container
    override val description: String
        get() = commonProduct.description
    override val name: String
        get() = commonProduct.name
    override val price: Float
        get() = commonProduct.price
    override val available: Boolean
        get() = commonProduct.available
    override val companyName: String
        get() = commonProduct.companyName
    override val imageUrl: String
        get() = commonProduct.imageUrl
    override val stock: Int
        get() = commonProduct.stock.minus(orderLine.quantity)
    override val quantityContainer: Int
        get() = commonProduct.quantityContainer
    override val quantityWeight: Int
        get() = commonProduct.quantityWeight
    override val unity: String
        get() = commonProduct.unity

    val quantity = orderLine.quantity

    fun getQuantityUnitySelected() = "${orderLine.quantity} $container"

    fun getAmount() = orderLine.quantity * price
}
