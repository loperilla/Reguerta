package com.reguerta.domain.usecase.app

import com.reguerta.data.firebase.firestore.products.ProductsService
import javax.inject.Inject
import timber.log.Timber

class PreloadProductsUseCase @Inject constructor(
    private val productsService: ProductsService
) {
    suspend operator fun invoke() {
        val result = productsService.getAllProducts()
        result.onSuccess { productList ->
            Timber.d("SYNC_Productos precargados: ${productList.size}")
            // Aquí podrías guardar en local si tienes DAO.
        }
        result.onFailure { error ->
            Timber.e(error, "Error al precargar productos")
        }
    }
}