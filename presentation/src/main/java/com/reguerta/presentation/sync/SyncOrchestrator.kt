package com.reguerta.presentation.sync

import timber.log.Timber
import com.google.firebase.Timestamp

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
            val remoteMillis = remoteTimestamps[tableKey]?.toDate()?.time ?: return@filter false
            val localMillis = localTimestamps[tableKey] ?: return@filter true
            remoteMillis > localMillis
        }
    }
}