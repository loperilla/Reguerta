package com.reguerta.data.firebase.firestore.products

import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.products
 * Created By Manuel Lopera on 24/2/24 at 15:21
 * All rights reserved 2024
 */
interface ProductsService {
    suspend fun getProducts(): Flow<Result<List<ProductModel>>>
    suspend fun deleteProduct(id: String)
    suspend fun addProduct(product: ProductModel, byteArray: ByteArray?): Result<Unit>
}