package com.reguerta.domain.usecase.containers

import android.util.Log
import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.containers.ContainersService
import javax.inject.Inject

class SyncContainersUseCase @Inject constructor(
    private val containersService: ContainersService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Log.d("SYNC_ContainersUseCase", "Iniciando sincronización de contenedores con timestamp: ${remoteTimestamp.seconds}")
        val result = containersService.getAllContainers()
        result.onSuccess {
            // TODO: Guardar contenedores en caché o base local si aplica
            dataStore.saveSyncTimestamp("containers", remoteTimestamp.seconds)
            Log.d("SYNC_ContainersUseCase", "Sincronización de contenedores completada correctamente.")
        }.onFailure {
            Log.e("SYNC_ContainersUseCase", "Error al sincronizar contenedores", it)
        }
    }
}