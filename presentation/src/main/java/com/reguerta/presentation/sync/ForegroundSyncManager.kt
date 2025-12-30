package com.reguerta.presentation.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

object ForegroundSyncManager {

    private var lastRunTimeMillis: Long = 0L

    // Throttle normal (sync OK) vs retry (sync failed)
    private const val MIN_INTERVAL_OK_MILLIS: Long = 30L * 60L * 1000L // 30 minutos
    private const val MIN_INTERVAL_FAIL_MILLIS: Long = 60L * 1000L      // 1 minuto

    private var lastRunSucceeded: Boolean = true

    private val mutex = Mutex()

    val syncRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun requestSyncFromAppLifecycle() {
        Timber.tag("SYNC_ForegroundSync").d("Emitiendo señal de sincronización (ts=%d)", System.currentTimeMillis())
        syncRequested.tryEmit(Unit)
    }

    suspend fun checkAndSyncIfNeeded(syncAction: suspend () -> Unit) {
        mutex.withLock {
            Timber.tag("FOREGROUND").i("checkAndSyncIfNeeded() invoked")
            val now = System.currentTimeMillis()
            val elapsed = now - lastRunTimeMillis
            val minInterval = if (lastRunSucceeded) MIN_INTERVAL_OK_MILLIS else MIN_INTERVAL_FAIL_MILLIS

            if (elapsed <= minInterval) {
                Timber.tag("FOREGROUND").d(
                    "check skipped: throttled (elapsed=%dms, min=%dms, lastOk=%s)",
                    elapsed, minInterval, lastRunSucceeded
                )
                Timber.tag("SYNC_ForegroundSync").d("Sync no necesario todavía (throttled).")
                return
            }

            // Marcamos el intento al inicio para evitar loops en caso de múltiples ON_START seguidos
            lastRunTimeMillis = now
            Timber.tag("FOREGROUND").i(
                "proceeding to foreground sync callback (elapsed=%dms, min=%dms, lastOk=%s)",
                elapsed, minInterval, lastRunSucceeded
            )
            Timber.tag("SYNC_ForegroundSync").d("Ha pasado suficiente tiempo, iniciando comprobación de datos…")

            lastRunSucceeded = try {
                syncAction()
                true
            } catch (t: Throwable) {
                Timber.tag("SYNC_ForegroundSync").e(t, "Foreground sync failed")
                false
            }
        }
    }
}