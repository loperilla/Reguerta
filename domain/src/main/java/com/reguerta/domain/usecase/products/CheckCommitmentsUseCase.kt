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

            // Obtener los productos disponibles y procesarlos
            var availableProducts: List<ProductModel> = emptyList()
            // Recopilar los productos disponibles


            productsService.getAvailableProducts().take(1).collect { result ->
                result.fold(
                    onSuccess = { productList ->
                        availableProducts = productList
                    },
                    onFailure = { error ->
                        // Manejo de errores al obtener productos
                        throw error
                    }
                )
            }
            val isCurrentWeekEven = weekTime.isEvenCurrentWeek()
            val userTypeConsumer = TypeConsumerUser.entries.find { it.value == currentUser.typeConsumer }
                ?: TypeConsumerUser.NONE

            val failureMessages = mutableListOf<String>()

            // Obtener los kilogramos de mangos y aguacates
            val kgMangoes = currentUser.tropical1 ?: 0.0
            val kgAvocados = currentUser.tropical2 ?: 0.0

            // Verificar el compromiso general
            if (userTypeConsumer.hasCommitmentThisWeek(isCurrentWeekEven)) {
                val commitmentProducts = availableProducts.filter {
                    it.container == ContainerType.COMMIT.value || it.container == ContainerType.RESIGN.value
                }
                val hasCommitmentProduct = productsInOrder.any { line ->
                    commitmentProducts.any { it.id == line.productId }
                }
                if (!hasCommitmentProduct) {
                    failureMessages.add("Has olvidado añadir la cesta compromiso. Si no deseas la cesta, debes añadir la renuncia.")
                }
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
