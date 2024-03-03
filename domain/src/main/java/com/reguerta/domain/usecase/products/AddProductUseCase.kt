package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductDTOModel
import com.reguerta.data.firebase.firestore.products.ProductsService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 2/3/24 at 14:04
 * All rights reserved 2024
 */
class AddProductUseCase @Inject constructor(
    private val service: ProductsService
) {
    suspend operator fun invoke(
        name: String,
        container: String,
        description: String,
        price: Float,
        available: Boolean,
        imageByteArray: ByteArray?,
        stock: Int,
        quantityContainer: Int,
        quantityWeight: Int,
        unity: String
    ): Result<Unit> {
        return try {
            val product = ProductDTOModel(
                name = name,
                container = container,
                description = description,
                price = price,
                available = available,
                stock = stock,
                quantityContainer = quantityContainer,
                quantityWeight = quantityWeight,
                unity = unity
            )
            service.addProduct(product, imageByteArray).fold(
                onSuccess = {
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}