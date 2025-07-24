package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.measures.MeasuresService
import com.reguerta.data.firebase.firestore.products.ProductModel
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.firestore.users.UserModel
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.enums.TypeProducerUser
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.domain.model.toDomain
import com.reguerta.domain.usecase.containers.toTypeProd
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 13/3/24 at 18:19
 * All rights reserved 2024
 */

class GetAvailableProductsUseCase @Inject constructor(
    private val productsService: ProductsService,
    private val usersService: UsersCollectionService,
    private val weekTime: WeekTime,
    private val authService: AuthService,
    private val measuresService: MeasuresService
) {
    private suspend fun retryGetUsersWithDelay(
        maxAttempts: Int = 5,
        delayMillis: Long = 2000
    ): List<UserModel> {
        repeat(maxAttempts - 1) {
            val result = usersService.getUserList().first().getOrNull()
            if (!result.isNullOrEmpty()) return result
            Timber.w("SYNC_DEBUG_USECASE - Retry $it: empty user list, retrying in $delayMillis ms")
            kotlinx.coroutines.delay(delayMillis)
        }
        // último intento
        return usersService.getUserList().first().getOrNull().orEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(forceFromServer: Boolean = false): Flow<List<CommonProduct>> {
        val currentUserResult = authService.checkCurrentLoggedUser()
        val currentUser = currentUserResult.getOrNull() ?: return flowOf(emptyList())

        return usersService.getUserList().flatMapLatest { usersResult ->
            val availableProducers = usersResult.fold(
                onSuccess = { userModelList ->
                    val isEvenWeek = weekTime.isEvenCurrentWeek()
                    Timber.i("SYNC_DEBUG_USECASE - Total userModelList size: ${userModelList.size}")
                    Timber.i("SYNC_TRACE_USECASE - Primeros IDs de usuarios cargados: ${userModelList.take(5).map { it.id }}")
                    Timber.i("SYNC_TRACE_USECASE - Todos los user IDs: ${userModelList.map { it.id }}")

                    val finalUserList = if (userModelList.isEmpty()) {
                        Timber.w("SYNC_DEBUG_USECASE - userModelList is empty, retrying with exponential delay")
                        retryGetUsersWithDelay()
                    } else {
                        userModelList.distinctBy { it.id }
                    }
                    Timber.i("SYNC_TRACE_USECASE - Productores detectados (sin filtrar): ${finalUserList.count { it.isProducer }}")

                    val filtered = finalUserList.filter { user ->
                        if (user.isProducer) {
                            val typeProducer = user.typeProducer?.toTypeProd() ?: TypeProducerUser.REGULAR
                            val hasCommitment = typeProducer.hasCommitmentThisWeek(isEvenWeek)
                            user.available ?: true && hasCommitment
                        } else {
                            false
                        }
                    }
                    Timber.i("SYNC_DEBUG_USECASE - Available producers after filtering: ${filtered.size}")
                    Timber.i("SYNC_TRACE_USECASE - IDs de productores disponibles: ${filtered.map { it.id }}")
                    filtered
                },
                onFailure = {
                    Timber.w("SYNC_DEBUG_USECASE - getUserList failed: $it")
                    emptyList()
                }
            )
            measuresService.getMeasures().flatMapLatest { result ->
                val measures = result.fold(
                    onSuccess = { measureModelList ->
                        measureModelList.map { it.toDomain() }
                    },
                    onFailure = {
                        Timber.w("SYNC_DEBUG_USECASE - getMeasures failed: $it")
                        emptyList()
                    }
                )

                productsService.getAvailableProducts(forceFromServer).map { products ->
                    Timber.i("SYNC_DEBUG_USECASE - Product models fetched: ${products.getOrNull()?.size ?: 0}")
                    Timber.i("SYNC_TRACE_USECASE - IDs productos crudos: ${products.getOrNull()?.take(5)?.map { it.id }}")
                    products.fold(
                        onSuccess = { productModelList ->
                            Timber.i("SYNC_DEBUG_USECASE - Filtered by available producers: ${productModelList.count { productModel -> availableProducers.any { it.id == productModel.userId } }}")
                            val mappedList = productModelList
                                .filter { productModel ->
                                    availableProducers.any { it.id == productModel.userId }
                                }
                                .map { productModel ->
                                    val modifiedProduct = modifyTropicalValues(productModel, currentUser, measures)
                                    modifiedProduct.toDomain()
                                }
                            Timber.i("SYNC_DEBUG_USECASE - Final available products toDomain: ${mappedList.size}")
                            Timber.i("SYNC_TRACE_USECASE - Productos finales a mostrar: ${mappedList.size} | IDs: ${mappedList.map { it.id }}")
                            mappedList
                        },
                        onFailure = {
                            emptyList()
                        }
                    )
                }
            }
        }
    }

    private fun modifyTropicalValues(product: ProductModel, currentUser: UserModel, measures: List<Measure>): ProductModel {
        val kgMangoes = currentUser.tropical1 ?: 0.0
        val kgAvocados = currentUser.tropical2 ?: 0.0
        val containerType = ContainerType.entries.firstOrNull { it.value.equals(product.container, ignoreCase = true) }

        return if ((containerType == ContainerType.COMMIT_MANGOES && kgMangoes > 0.0) || (containerType == ContainerType.COMMIT_AVOCADOS && kgAvocados > 0.0)) {
            val kilos = if (containerType == ContainerType.COMMIT_MANGOES) kgMangoes else kgAvocados
            val measure = getMeasureByName(product.unity.orEmpty(), measures)

            product.copy(
                quantityWeight = kilos.roundToInt(),
                price = (product.price ?: 0.0f) * kilos.toFloat(),
                unity = if (measure != null && kilos > 1) measure.plural else product.unity
            )
        } else {
            product
        }
    }

    private fun getMeasureByName(name: String, measures: List<Measure>): Measure? {
        return measures.firstOrNull { it.name == name || it.abbreviation == name || it.plural == name }
    }

    suspend fun getAllProductsDirect(): Result<List<CommonProduct>> {
        return productsService.getAllProducts().map { list ->
            list.map { it.toDomain() }
        }
    }
}
