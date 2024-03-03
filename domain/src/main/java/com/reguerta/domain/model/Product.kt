package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.products.ProductDTOModel
import com.reguerta.data.firebase.firestore.products.ProductModel

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 25/2/24 at 11:25
 * All rights reserved 2024
 */
data class Product(
    val id: String = "",
    val container: String = "",
    val description: String = "",
    val name: String = "",
    val price: Float = 0.0f,
    val available: Boolean = false,
    val companyName: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val quantityContainer: Int = 0,
    val quantityWeight: Int = 0,
    val unity: String = ""
)

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
fun ProductModel.toDomain(): Product = Product(
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