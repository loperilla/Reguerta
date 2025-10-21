package com.reguerta.domain.usecase.measures

import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.measures.MeasuresService
import timber.log.Timber
import javax.inject.Inject

class SyncMeasuresUseCase @Inject constructor(
    private val measuresService: MeasuresService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Timber.tag("SYNC_MeasuresUseCase").d("Iniciando sincronización de medidas con timestamp: ${remoteTimestamp.seconds}")
        val result = measuresService.getAllMeasures()
        result.onSuccess {
            // TODO: Guardar medidas en caché o base local si aplica
            dataStore.saveSyncTimestamp("measures", remoteTimestamp.seconds)
            Timber.tag("SYNC_MeasuresUseCase").d("Sincronización de medidas completada correctamente.")
        }.onFailure {
            Timber.tag("SYNC_MeasuresUseCase").e(it, "Error al sincronizar medidas")
        }
    }
}