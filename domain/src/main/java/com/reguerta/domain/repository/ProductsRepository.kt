package com.reguerta.domain.repository

import com.reguerta.domain.model.CommonProduct
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun preloadProducts()
    fun getAvailableProducts(forceFromServer: Boolean): Flow<List<CommonProduct>>
}