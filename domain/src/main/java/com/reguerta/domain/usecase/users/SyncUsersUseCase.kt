package com.reguerta.domain.usecase.users

import com.google.firebase.Timestamp
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class SyncUsersUseCase @Inject constructor(
    private val usersService: UsersCollectionService,
    private val dataStore: ReguertaDataStore
) {
    /**
     * Sincroniza la colección de usuarios y, si el fetch fue correcto,
     * guarda el timestamp remoto (en segundos) en DataStore bajo la clave "users".
     *
     * Nota: UsersCollectionService.getUserList() expone Flow<Result<List<UserModel>>>.
     * Para alinear el comportamiento con el resto de casos de uso de sync,
     * consumimos SOLO la primera emisión (first()).
     */
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Timber.tag("SYNC_UsersUseCase").d("Iniciando sincronización de usuarios con timestamp: ${remoteTimestamp.seconds}")
        try {
            val result = usersService.getUserList().first()
            result
                .onSuccess { list ->
                    dataStore.saveSyncTimestamp("users", remoteTimestamp.seconds) // segundos
                    val saved = dataStore.getSyncTimestamp("users")
                    Timber.tag("SyncDebug").d("Timestamp guardado para users: $saved")
                    Timber.tag("SYNC_UsersUseCase").d("Sincronización de usuarios completada correctamente. count=${list.size}")
                }
                .onFailure { e ->
                    Timber.tag("SYNC_UsersUseCase").e(e, "Error al sincronizar usuarios")
                }
        } catch (t: Throwable) {
            Timber.tag("SYNC_UsersUseCase").e(t, "Excepción al sincronizar usuarios")
        }
    }
}