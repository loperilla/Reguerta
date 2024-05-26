package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 25/3/24 at 19:56
 * All rights reserved 2024
 */

class UpdateProductStockUseCase @Inject constructor(
    private val productsService: ProductsService
) {
    suspend operator fun invoke(id: String, newStock: Int): Result<Unit> {
        return productsService.updateStockProduct(id, newStock)
    }
}