package com.reguerta.domain.usecase.app

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import timber.log.Timber

class PreloadCriticalDataUseCase @Inject constructor(
    private val usersService: UsersCollectionService
    // FUTURO: Aquí puedes inyectar también OrdersService, OrderLinesService, etc.
) {
    suspend operator fun invoke() {
        // Precarga los usuarios para que Mi Pedido funcione tras la instalación limpia.
        val result = usersService.getUserList().first()
        result.onSuccess { users ->
            Timber.d("SYNC_Usuarios precargados: ${users.size}")
            // Aquí puedes guardar en local si tienes DAO.
        }
        result.onFailure { error ->
            Timber.e(error, "Error al precargar usuarios")
        }
        // FUTURO: Añade aquí la llamada a los servicios de orders y orderlines para
        // precargar esos datos críticos también tras la primera instalación.
    }
}