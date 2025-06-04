package com.reguerta.domain.repository

interface ProductsRepository {
    suspend fun preloadProducts()
}