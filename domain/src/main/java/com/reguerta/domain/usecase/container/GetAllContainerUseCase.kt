package com.reguerta.domain.usecase.container

import com.reguerta.data.firebase.firestore.container.ContainerService
import com.reguerta.domain.model.Container
import com.reguerta.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.container
 * Created By Manuel Lopera on 2/3/24 at 12:54
 * All rights reserved 2024
 */
class GetAllContainerUseCase @Inject constructor(
    private val containerService: ContainerService
) {
    suspend operator fun invoke(): Flow<List<Container>> {
        return containerService.getContainers().map {
            it.fold(
                onSuccess = { containerModelList ->
                    containerModelList.map { containerModel ->
                        containerModel.toDomain()
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        }
    }
}