package com.reguerta.domain.usecase.measures

import android.util.Log
import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.measures.MeasuresService
import javax.inject.Inject

class SyncMeasuresUseCase @Inject constructor(
    private val measuresService: MeasuresService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Log.d("SYNC_MeasuresUseCase", "Iniciando sincronización de medidas con timestamp: ${remoteTimestamp.seconds}")
        val result = measuresService.getAllMeasures()
        result.onSuccess {
            // TODO: Guardar medidas en caché o base local si aplica
            dataStore.saveSyncTimestamp("measures", remoteTimestamp.seconds)
            Log.d("SYNC_MeasuresUseCase", "Sincronización de medidas completada correctamente.")
        }.onFailure {
            Log.e("SYNC_MeasuresUseCase", "Error al sincronizar medidas", it)
        }
    }
}