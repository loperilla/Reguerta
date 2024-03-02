package com.reguerta.data.firebase.firestore.container

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.container
 * Created By Manuel Lopera on 2/3/24 at 12:49
 * All rights reserved 2024
 */
class ContainerServiceImpl @Inject constructor(
    private val collection: CollectionReference
) : ContainerService {
    override suspend fun getContainers(): Flow<Result<List<ContainerModel>>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                trySend(Result.failure(error))
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let { query ->
                val containerList = mutableListOf<ContainerModel>()
                query.documents.forEach { document ->
                    val containerModel = document.toObject(ContainerModel::class.java)
                    containerModel?.let { model ->
                        model.id = document.id
                        containerList.add(model)
                    }
                }
                trySend(Result.success(containerList))
            }
        }
        awaitClose {
            subscription.remove()
        }
    }
}