package com.reguerta.domain.usecase.container

import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.firestore.container.ContainerService
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.enums.TypeProducerUser
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFilteredContainersUseCase @Inject constructor(
    private val containerService: ContainerService,
    private val authService: AuthService
) {
    suspend operator fun invoke(): Flow<List<Container>> {
        val currentUserResult = authService.checkCurrentLoggedUser()
        val currentUser = currentUserResult.getOrNull() ?: return flowOf(emptyList())
        val isTropicalProducer = currentUser.companyName == "Los Tropicales"
        val producerType = currentUser.typeProducer?.toTypeProd() ?: TypeProducerUser.REGULAR

        return containerService.getContainers().map { result ->
            result.fold(
                onSuccess = { containerModelList ->
                    val containers = containerModelList.map { containerModel ->
                        containerModel.toDomain()
                    }
                    filterByProducerType(containers, producerType, isTropicalProducer)
                },
                onFailure = { emptyList() }
            )
        }
    }

    private fun filterByProducerType(
        containers: List<Container>,
        producerType: TypeProducerUser,
        isTropicalProducer: Boolean
    ): List<Container> {
        val allowedContainers: List<ContainerType> = when (producerType) {
            TypeProducerUser.REGULAR -> ContainerType.sharedContainers()
            TypeProducerUser.ODD, TypeProducerUser.EVEN -> ContainerType.forMainProducers()
            TypeProducerUser.SHOP -> if (isTropicalProducer) {
                ContainerType.forTropicalProducer()
            } else {
                ContainerType.sharedContainers()
            }
        }

        return containers.filter { container ->
            allowedContainers.any { it.value == container.name }
        }
    }
}

fun String.toTypeProd(): TypeProducerUser {
    return TypeProducerUser.entries.find { it.value == this.lowercase() } ?: TypeProducerUser.REGULAR
}


