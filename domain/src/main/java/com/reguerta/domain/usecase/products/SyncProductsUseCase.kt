package com.reguerta.domain.usecase.products

import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.products.ProductsService
import timber.log.Timber
import javax.inject.Inject

class SyncProductsUseCase @Inject constructor(
    private val productsService: ProductsService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Timber.tag("SYNC_ProductsUseCase").d("Iniciando sincronización de productos con timestamp: ${remoteTimestamp.seconds}")
        val result = productsService.getAllProducts()
        result.onSuccess {
            dataStore.saveSyncTimestamp("products", remoteTimestamp.seconds)
            val saved = dataStore.getSyncTimestamp("products")
            Timber.tag("SyncDebug").d("Timestamp guardado para products: $saved")
            Timber.tag("SYNC_ProductsUseCase").d("Sincronización de productos completada correctamente.")
        }.onFailure {
            Timber.tag("SYNC_ProductsUseCase").e(it, "Error al sincronizar productos")
        }
    }
}

