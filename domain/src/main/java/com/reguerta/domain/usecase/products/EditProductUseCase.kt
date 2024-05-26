package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.toDto
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 3/3/24 at 16:20
 * All rights reserved 2024
 */

class EditProductUseCase @Inject constructor(
    private val productsService: ProductsService
) {
    suspend operator fun invoke(
        id: String,
        product: CommonProduct,
        imageByteArray: ByteArray?
    ): Result<Unit> {
        return try {
            productsService.editProduct(
                id,
                product.toDto(),
                imageByteArray
            ).fold(
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
