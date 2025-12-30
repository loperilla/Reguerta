package com.reguerta.presentation.sync

import timber.log.Timber
import com.google.firebase.Timestamp
import kotlin.IllegalStateException

object SyncOrchestrator {
    suspend fun runSyncIfNeeded(
        getRemoteTimestamps: suspend () -> Map<String, Timestamp>,
        getLocalTimestamps: suspend () -> Map<String, Long>,
        getCriticalTables: () -> List<String>,
        syncActions: Map<String, suspend (Timestamp) -> Unit>
    ) {
        val remoteTimestamps = getRemoteTimestamps()
        val localTimestamps = getLocalTimestamps()
        val criticalTables = getCriticalTables()

        // Si no podemos leer timestamps remotos (p.ej. fallo de config/auth), NO debemos hacer no-op silencioso.
        if (remoteTimestamps.isEmpty()) {
            Timber.tag("SYNC_SyncOrchestrator").e("remoteTimestamps vacío → abortando sync (evita 'no hay pedidos' por error silenciado)")
            throw IllegalStateException("remoteTimestamps vacío")
        }

        // Validar que todas las tablas críticas tienen timestamp remoto; si falta alguno, abortamos para reintentar.
        val missingRemoteKeys = criticalTables.filter { it !in remoteTimestamps.keys }
        if (missingRemoteKeys.isNotEmpty()) {
            Timber.tag("SYNC_SyncOrchestrator").e("Faltan timestamps remotos para tablas críticas: %s", missingRemoteKeys)
            throw IllegalStateException("Missing remote timestamps: $missingRemoteKeys")
        }

        val tablesToSync = getTablesToSync(remoteTimestamps, localTimestamps, criticalTables)

        Timber.tag("SYNC_SyncOrchestrator").d("Tablas a sincronizar: $tablesToSync")

        tablesToSync.forEach { tableKey ->
            remoteTimestamps[tableKey]?.let { timestamp ->
                syncActions[tableKey]?.invoke(timestamp)
            }
        }
    }

    private fun getTablesToSync(
        remoteTimestamps: Map<String, Timestamp>,
        localTimestamps: Map<String, Long>,
        criticalTables: List<String>
    ): List<String> {
        return criticalTables.filter { tableKey ->
            val remoteMillis = remoteTimestamps.getValue(tableKey).toDate().time
            val localMillis = localTimestamps[tableKey] ?: return@filter true
            Timber.tag("SYNC_SyncOrchestrator").d(
                "compare %s: remote=%d local=%d → %s",
                tableKey,
                remoteMillis,
                localMillis,
                if (remoteMillis > localMillis) "SYNC" else "OK"
            )
            remoteMillis > localMillis
        }
    }
}