package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.toDomain
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 3/3/24 at 15:09
 * All rights reserved 2024
 */

class GetProductByIdUseCase @Inject constructor(
    private val productService: ProductsService
) {
    suspend operator fun invoke(
        id: String
    ): Result<CommonProduct> {
        return productService.getProductById(id).fold(
            onSuccess = { Result.success(it.toDomain()) },
            onFailure = { Result.failure(it) }
        )
    }
}