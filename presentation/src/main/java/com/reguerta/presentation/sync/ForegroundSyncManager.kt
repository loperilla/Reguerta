package com.reguerta.presentation.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber

object ForegroundSyncManager {

    private var lastCheckTimeMillis: Long = 0L
    private const val MIN_INTERVAL_MILLIS = 30 * 60 * 1000 // 30 minutos
    private var isSyncing = false

    val syncRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun requestSyncFromAppLifecycle() {
        Timber.tag("SYNC_ForegroundSync").d("Emitiendo señal de sincronización")
        syncRequested.tryEmit(Unit)
    }

    suspend fun checkAndSyncIfNeeded(syncAction: suspend () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastCheckTimeMillis > MIN_INTERVAL_MILLIS && !isSyncing) {
            isSyncing = true
            lastCheckTimeMillis = now
            Timber.tag("SYNC_ForegroundSync").d("Ha pasado suficiente tiempo, iniciando comprobación de datos...")
            try {
                syncAction()
            } finally {
                isSyncing = false
            }
        } else {
            Timber.tag("SYNC_ForegroundSync").d("Sync no necesario todavía o ya en curso.")
        }
    }
}