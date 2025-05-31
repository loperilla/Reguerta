package com.reguerta.domain.usecase.products

import android.util.Log
import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.products.ProductsService
import javax.inject.Inject

class SyncProductsUseCase @Inject constructor(
    private val productsService: ProductsService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Log.d("SYNC_ProductsUseCase", "Iniciando sincronización de productos con timestamp: ${remoteTimestamp.seconds}")
        val result = productsService.getAllProducts()
        result.onSuccess {
            dataStore.saveSyncTimestamp("products", remoteTimestamp.seconds)
            val saved = dataStore.getSyncTimestamp("products")
            Log.d("SyncDebug", "Timestamp guardado para products: $saved")
            Log.d("SYNC_ProductsUseCase", "Sincronización de productos completada correctamente.")
        }.onFailure {
            Log.e("SYNC_ProductsUseCase", "Error al sincronizar productos", it)
        }
    }
}