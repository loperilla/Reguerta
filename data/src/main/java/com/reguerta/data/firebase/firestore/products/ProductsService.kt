package com.reguerta.data.firebase.firestore.products

import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.products
 * Created By Manuel Lopera on 24/2/24 at 15:21
 * All rights reserved 2024
 */

interface ProductsService {
    suspend fun getProductsByUserId(): Flow<Result<List<ProductModel>>>
    suspend fun getAvailableProducts(): Flow<Result<List<ProductModel>>>
    suspend fun deleteProduct(id: String)
    suspend fun addProduct(product: ProductDTOModel, byteArray: ByteArray?): Result<Unit>
    suspend fun getProductById(id: String): Result<ProductModel>
    suspend fun editProduct(id: String, product: ProductDTOModel, byteArray: ByteArray?): Result<Unit>
    suspend fun updateStockProduct(id: String, newStock: Int): Result<Unit>
}