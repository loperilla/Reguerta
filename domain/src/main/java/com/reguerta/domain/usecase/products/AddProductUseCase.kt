package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.toDto
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
        commonProduct: CommonProduct,
        imageByteArray: ByteArray?,
    ): Result<Unit> {
        return try {
            service.addProduct(commonProduct.toDto(), imageByteArray).fold(
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