package com.reguerta.domain.usecase.app

import com.google.firebase.Timestamp
import com.reguerta.domain.usecase.users.SyncUsersUseCase
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import com.reguerta.domain.usecase.containers.SyncContainersUseCase
import com.reguerta.domain.usecase.measures.SyncMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.SyncOrdersAndOrderLinesUseCase
import javax.inject.Inject
import timber.log.Timber

/**
 * Orquestador de precarga para un arranque "frío".
 * Delegamos en los casos de uso de sincronización y sellamos los timestamps locales
 * con el tiempo actual para evitar bloqueos iniciales del botón "Mi pedido".
 */
class PreloadCriticalDataUseCase @Inject constructor(
    private val syncUsersUseCase: SyncUsersUseCase,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val syncContainersUseCase: SyncContainersUseCase,
    private val syncMeasuresUseCase: SyncMeasuresUseCase,
    private val syncOrdersAndOrderLinesUseCase: SyncOrdersAndOrderLinesUseCase,
) {
    suspend operator fun invoke() {
        val ts = Timestamp.now()
        Timber.d("SYNC_Preload: start ts=%s", ts.seconds)

        runCatching { syncUsersUseCase(ts) }
            .onSuccess { Timber.d("SYNC_Preload: users ✓") }
            .onFailure { Timber.e(it, "SYNC_Preload: users ✗") }

        runCatching { syncProductsUseCase(ts) }
            .onSuccess { Timber.d("SYNC_Preload: products ✓") }
            .onFailure { Timber.e(it, "SYNC_Preload: products ✗") }

        runCatching { syncContainersUseCase(ts) }
            .onSuccess { Timber.d("SYNC_Preload: containers ✓") }
            .onFailure { Timber.e(it, "SYNC_Preload: containers ✗") }

        runCatching { syncMeasuresUseCase(ts) }
            .onSuccess { Timber.d("SYNC_Preload: measures ✓") }
            .onFailure { Timber.e(it, "SYNC_Preload: measures ✗") }

        runCatching { syncOrdersAndOrderLinesUseCase(ts) }
            .onSuccess { Timber.d("SYNC_Preload: orders & orderlines ✓") }
            .onFailure { Timber.e(it, "SYNC_Preload: orders & orderlines ✗") }

    }
}