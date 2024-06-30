package com.reguerta.domain.usecase.products

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.mapper.toDomain
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

class GetAvailableProductsUseCase @Inject constructor(
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
