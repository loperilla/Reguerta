package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 1/3/24 at 16:40
 * All rights reserved 2024
 */

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductsService
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return try {
            repository.deleteProduct(id)
            Result.success(true)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
