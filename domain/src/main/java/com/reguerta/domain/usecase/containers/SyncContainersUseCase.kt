package com.reguerta.domain.usecase.containers

import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.containers.ContainersService
import timber.log.Timber
import javax.inject.Inject

class SyncContainersUseCase @Inject constructor(
    private val containersService: ContainersService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Timber.tag("SYNC_ContainersUseCase").d("Iniciando sincronización de contenedores con timestamp: ${remoteTimestamp.seconds}")
        val result = containersService.getAllContainers()
        result.onSuccess {
            // TODO: Guardar contenedores en caché o base local si aplica
            dataStore.saveSyncTimestamp("containers", remoteTimestamp.seconds)
            Timber.tag("SYNC_ContainersUseCase").d("Sincronización de contenedores completada correctamente.")
        }.onFailure {
            Timber.tag("SYNC_ContainersUseCase").e(it, "Error al sincronizar contenedores")
        }
    }
}