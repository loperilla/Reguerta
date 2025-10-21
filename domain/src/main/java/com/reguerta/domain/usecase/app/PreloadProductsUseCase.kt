package com.reguerta.domain.usecase.app

import com.google.firebase.Timestamp
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import javax.inject.Inject
import timber.log.Timber

@Deprecated(
    message = "PreloadProductsUseCase será retirado. Usa PreloadCriticalDataUseCase para arranque inicial o SyncProductsUseCase para sincronizar productos.",
    level = DeprecationLevel.WARNING
)
class PreloadProductsUseCase @Inject constructor(
    private val syncProductsUseCase: SyncProductsUseCase
) {
    /**
     * Compatibilidad temporal: delega en SyncProductsUseCase con un timestamp 'now'.
     * Evita duplicar lógica y asegura que el guard de frescura use segundos.
     */
    suspend operator fun invoke() {
        val ts = Timestamp.now()
        Timber.d("SYNC_PreloadProducts: delegando en SyncProductsUseCase ts=%s", ts.seconds)
        runCatching { syncProductsUseCase(ts) }
            .onSuccess { Timber.d("SYNC_PreloadProducts: ✓") }
            .onFailure { Timber.e(it, "SYNC_PreloadProducts: ✗") }
    }
}