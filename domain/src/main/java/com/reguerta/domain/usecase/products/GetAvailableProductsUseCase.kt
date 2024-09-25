package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.measures.MeasureService
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
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.products
 * Created By Manuel Lopera on 13/3/24 at 18:19
 * All rights reserved 2024
 */

class GetAvailableProductsUseCase1 @Inject constructor(
    private val productsService: ProductsService
) {
    suspend operator fun invoke(): Flow<List<CommonProduct>> =
        productsService.getAvailableProducts().map {
            it.fold(
                onSuccess = { productModelList ->
                    productModelList.map { productModel ->
                        productModel.toDomain()
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        }
}

class GetAvailableProductsUseCase2 @Inject constructor(
    private val productsService: ProductsService,
    private val usersService: UsersCollectionService
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<CommonProduct>> {
        return usersService.getUserList().flatMapLatest { usersResult ->
            val availableProducers = usersResult.fold(
                onSuccess = { userModelList ->
                    userModelList.filter { it.isProducer && it.available == true }.map { it.id }
                },
                onFailure = {
                    emptyList()
                }
            )

            productsService.getAvailableProducts().map { products ->
                products.fold(
                    onSuccess = { productModelList ->
                        productModelList.filter { productModel ->
                            productModel.userId in availableProducers
                        }.map { productModel ->
                            productModel.toDomain()
                        }
                    },
                    onFailure = {
                        emptyList()
                    }
                )
            }
        }
    }
}

class GetAvailableProductsUseCase3 @Inject constructor(
    private val productsService: ProductsService,
    private val usersService: UsersCollectionService,
    private val weekTime: WeekTime
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<CommonProduct>> {
        return usersService.getUserList().flatMapLatest { usersResult ->
            val availableProducers = usersResult.fold(
                onSuccess = { userModelList ->
                    userModelList.filter { it.isProducer && it.available == true }
                },
                onFailure = {
                    emptyList()
                }
            )

            val isWeekEven = weekTime.isEvenCurrentWeek()

            productsService.getAvailableProducts().map { products ->
                products.fold(
                    onSuccess = { productModelList ->
                        productModelList.filter { productModel ->
                            val producer = availableProducers.find { it.id == productModel.userId }
                            if (producer != null) {
                                when {
                                    producer.typeProducer == "par" && !isWeekEven -> {
                                        productModel.container != "Compromiso" && productModel.container != "Renuncia"
                                    }
                                    producer.typeProducer == "impar" && isWeekEven -> {
                                        productModel.container != "Compromiso" && productModel.container != "Renuncia"
                                    }
                                    else -> true
                                }
                            } else {
                                false
                            }
                        }.map { productModel ->
                            productModel.toDomain()
                        }
                    },
                    onFailure = {
                        emptyList()
                    }
                )
            }
        }
    }
}

class GetAvailableProductsUseCase4 @Inject constructor(
    private val productsService: ProductsService,
    private val usersService: UsersCollectionService,
    private val weekTime: WeekTime,
    private val authService: AuthService
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<CommonProduct>> {
        val currentUserResult = authService.checkCurrentLoggedUser()
        val currentUser = currentUserResult.getOrNull() ?: return flowOf(emptyList())

        return usersService.getUserList().flatMapLatest { usersResult ->
            val availableProducers = usersResult.fold(
                onSuccess = { userModelList ->
                    userModelList.filter { it.isProducer && it.available == true }
                },
                onFailure = {
                    emptyList()
                }
            )

            val isWeekEven = weekTime.isEvenCurrentWeek()

            productsService.getAvailableProducts().map { products ->
                products.fold(
                    onSuccess = { productModelList ->
                        productModelList.filter { productModel ->
                            val producer = availableProducers.find { it.id == productModel.userId }
                            if (producer != null) {
                                when {
                                    producer.typeProducer == "par" && !isWeekEven -> {
                                        productModel.container != "Compromiso" && productModel.container != "Renuncia"
                                    }
                                    producer.typeProducer == "impar" && isWeekEven -> {
                                        productModel.container != "Compromiso" && productModel.container != "Renuncia"
                                    }
                                    else -> true
                                }
                            } else {
                                false
                            }
                        }
                            .filter { productModel ->
                                !(((currentUser.numResignations ?: 0) >= 2 && productModel.container == "Renuncia"))
                            }
                            .map { productModel ->
                                productModel.toDomain()
                            }
                    },
                    onFailure = {
                        emptyList()
                    }
                )
            }
        }
    }
}
class GetAvailableProductsUseCase @Inject constructor(
    private val productsService: ProductsService,
    private val usersService: UsersCollectionService,
    private val weekTime: WeekTime,
    private val authService: AuthService,
    private val measureService: MeasureService
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<CommonProduct>> {
        // Obtener el usuario autenticado
        val currentUserResult = authService.checkCurrentLoggedUser()
        val currentUser = currentUserResult.getOrNull() ?: return flowOf(emptyList())

        // Obtener la lista de usuarios
        return usersService.getUserList().flatMapLatest { usersResult ->
            // Procesar los productores disponibles con filtro por semana par/impar
            val availableProducers = usersResult.fold(
                onSuccess = { userModelList ->
                    val isEvenWeek = weekTime.isEvenCurrentWeek()

                    // Filtrar productores disponibles que tienen compromiso esta semana
                    userModelList.filter { user ->
                        user.isProducer && user.available ?: true &&
                                TypeProducerUser.valueOf(user.typeProducer.orEmpty()).hasCommitmentThisWeek(isEvenWeek)
                    }
                },
                onFailure = {
                    emptyList()
                }
            )

            // Obtener todas las medidas del servicio de medidas
            measureService.getMeasures().flatMapLatest { result ->
                val measures = result.fold(
                    onSuccess = { measureModelList ->
                        measureModelList.map { it.toDomain() } // Convierte las medidas usando el toDomain de MeasureModel
                    },
                    onFailure = { emptyList() }
                )

                // Obtener los productos disponibles
                productsService.getAvailableProducts().map { products ->
                    products.fold(
                        onSuccess = { productModelList ->
                            // Filtrar los productos según los productores disponibles
                            productModelList.filter { productModel ->
                                availableProducers.any { it.id == productModel.userId }
                            }
                                // Modificar valores tropicales de los productos
                                .map { productModel ->
                                    modifyTropicalValues(productModel, currentUser, measures)
                                }
                                // Convertir a CommonProduct (suponiendo que esto es necesario para la UI)
                                .map { productModel ->
                                    productModel.toDomain() // Conversión a CommonProduct
                                }
                        },
                        onFailure = {
                            emptyList()
                        }
                    )
                }
            }
        }
    }

    // Función para modificar los valores tropicales usando la lista de medidas
    private fun modifyTropicalValues(product: ProductModel, currentUser: UserModel, measures: List<Measure>): ProductModel {
        val kgMangoes = currentUser.tropical1 ?: 0.0
        val kgAvocados = currentUser.tropical2 ?: 0.0

        // Verificar si es un producto de compromiso (mangos o aguacates) y si hay stock
        return if ((product.container == ContainerType.COMMIT_MANGOES.value && kgMangoes > 0.0) ||
            (product.container == ContainerType.COMMIT_AVOCADOS.value && kgAvocados > 0.0)) {

            val kilos = if (product.container == ContainerType.COMMIT_MANGOES.value) kgMangoes else kgAvocados

            // Crear una nueva instancia de ProductModel usando copy
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


    // Función para buscar la medida por nombre, abreviación o plural
    private fun getMeasureByName(name: String, measures: List<Measure>): Measure? {
        return measures.firstOrNull { it.name == name || it.abbreviation == name || it.plural == name }
    }
}

