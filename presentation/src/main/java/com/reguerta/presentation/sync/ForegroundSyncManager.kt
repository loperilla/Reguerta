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
        Timber.tag("FOREGROUND").i("checkAndSyncIfNeeded() invoked")
        val now = System.currentTimeMillis()
        val elapsed = now - lastCheckTimeMillis
        if (elapsed > MIN_INTERVAL_MILLIS && !isSyncing) {
            isSyncing = true
            lastCheckTimeMillis = now
            Timber.tag("FOREGROUND").i("proceeding to foreground sync callback (elapsed=%dms, min=%dms)", elapsed, MIN_INTERVAL_MILLIS)
            Timber.tag("SYNC_ForegroundSync").d("Ha pasado suficiente tiempo, iniciando comprobación de datos…")
            try {
                syncAction()
            } finally {
                isSyncing = false
            }
        } else {
            Timber.tag("FOREGROUND").d("check skipped: throttled or syncing (elapsed=%dms, min=%dms, isSyncing=%s)", elapsed, MIN_INTERVAL_MILLIS, isSyncing)
            Timber.tag("SYNC_ForegroundSync").d("Sync no necesario todavía o ya en curso.")
        }
    }
}