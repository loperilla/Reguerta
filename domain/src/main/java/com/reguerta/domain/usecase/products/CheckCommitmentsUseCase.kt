package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.products.ProductModel
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.enums.TypeConsumerUser
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckCommitmentsUseCase @Inject constructor(
    private val productsService: ProductsService,
    private val authService: AuthService,
    private val weekTime: WeekTime
) {

    suspend operator fun invoke(
        productsInOrder: List<OrderLineProduct>
    ): Result<String?> = withContext(Dispatchers.IO) {
        try {
            val currentUserResult = authService.checkCurrentLoggedUser()
            val currentUser = currentUserResult.getOrNull() ?: return@withContext Result.failure(Exception("Usuario no autenticado"))

            var availableProducts: List<ProductModel> = emptyList()

            productsService.getAvailableProducts(forceFromServer = true).take(1).collect { result ->
                result.fold(
                    onSuccess = { productList ->
                        availableProducts = productList
                    },
                    onFailure = { error ->
                        throw error
                    }
                )
            }
            val isCurrentWeekEven = weekTime.isEvenCurrentWeek()
            val userTypeConsumer = TypeConsumerUser.entries.find { it.value == currentUser.typeConsumer } ?: TypeConsumerUser.NONE
            val failureMessages = mutableListOf<String>()

            // Obtener los kilogramos de mangos y aguacates
            val kgMangoes = currentUser.tropical1 ?: 0.0
            val kgAvocados = currentUser.tropical2 ?: 0.0

            // Verificar el compromiso general (ventana por paridad)
            val isCommitmentWindow = userTypeConsumer.isCommitmentAllowedThisWeek(isCurrentWeekEven)

            val commitmentProducts = availableProducts.filter {
                it.container == ContainerType.COMMIT.value || it.container == ContainerType.RESIGN.value
            }
            val hasCommit = productsInOrder.any { line ->
                commitmentProducts.any { it.id == line.productId && it.container == ContainerType.COMMIT.value }
            }
            val hasResign = productsInOrder.any { line ->
                commitmentProducts.any { it.id == line.productId && it.container == ContainerType.RESIGN.value }
            }

            // 1) Si ES su semana (p. ej., consumidor PAR en semana PAR), exigir COMMIT o RESIGN
            if (isCommitmentWindow) {
                if (!hasCommit && !hasResign) {
                    failureMessages.add("Esta semana te corresponde compromiso: añade la cesta de compromiso o la renuncia.")
                }
            }
            // Fuera de su semana: libertad total (no se prohíbe COMMIT/RESIGN)

            // 3) Evitar COMMIT y RESIGN a la vez
            if (hasCommit && hasResign) {
                failureMessages.add("No puedes añadir a la vez la cesta de compromiso y la renuncia. Elige solo una.")
            }

            // Verificar compromiso de mangos
            if (kgMangoes > 0) {
                val mangoProducts = availableProducts.filter {
                    it.container == ContainerType.COMMIT_MANGOES.value
                }
                if (mangoProducts.isNotEmpty()) {
                    val hasMangoCommit = productsInOrder.any { line ->
                        mangoProducts.any { it.id == line.productId }
                    }
                    if (!hasMangoCommit) {
                        failureMessages.add("Has olvidado incluir tu compromiso de mangos en el carrito.")
                    }
                }
            }

            // Verificar compromiso de aguacates
            if (kgAvocados > 0) {
                val avocadoProducts = availableProducts.filter {
                    it.container == ContainerType.COMMIT_AVOCADOS.value
                }
                if (avocadoProducts.isNotEmpty()) {
                    val hasAvocadoCommit = productsInOrder.any { line ->
                        avocadoProducts.any { it.id == line.productId }
                    }
                    if (!hasAvocadoCommit) {
                        failureMessages.add("Has olvidado incluir tu compromiso de aguacates en el carrito.")
                    }
                }
            }

            return@withContext if (failureMessages.isEmpty()) {
                Result.success(null)
            } else {
                Result.failure(Exception(failureMessages.joinToString("\n")))
            }

        } catch (ex: Exception) {
            return@withContext Result.failure(ex)
        }
    }
}
