package com.reguerta.data.firebase.firestore.containers

import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.container
 * Created By Manuel Lopera on 2/3/24 at 12:48
 * All rights reserved 2024
 */

interface ContainersService {
    suspend fun getAllContainers(): Result<List<ContainerModel>>
}