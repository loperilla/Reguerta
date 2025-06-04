package com.reguerta.domain.repository

import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    // Inyecta aquí tus DAOs, servicios remotos, etc. Por ejemplo:
    // private val firestoreService: FirestoreService,
    // private val localDao: LocalProductDao
) : ProductsRepository {

    override suspend fun preloadProducts() {
        // Pendiente de implementar en la refactorización general.
        // Actualmente la precarga de productos se gestiona directamente desde el UseCase,
        // que inyecta y usa ProductsService. Esta función se implementará correctamente tras la migración a Clean Architecture.
    }
}