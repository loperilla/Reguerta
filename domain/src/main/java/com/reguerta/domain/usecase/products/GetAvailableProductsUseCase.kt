package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 13/3/24 at 18:19
 * All rights reserved 2024
 */

class GetAvailableProductsUseCase @Inject constructor(
    private val productsService: ProductsService
) {
    suspend operator fun invoke(): Flow<List<CommonProduct>> =
        productsService.getAvailableProducts().map {
            it.fold(
                onSuccess = { productModelList ->
                    productModelList.map { productModel ->
                        productModel.toDomain()
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        }
}