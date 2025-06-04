package com.reguerta.data.firebase.firestore.containers

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.container
 * Created By Manuel Lopera on 2/3/24 at 12:49
 * All rights reserved 2024
 */

class ContainersServiceImpl @Inject constructor(
    private val collection: CollectionReference
) : ContainersService {

    override suspend fun getAllContainers(): Result<List<ContainerModel>> {
        return try {
            val snapshot = collection
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val containerList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ContainerModel::class.java)?.apply { id = doc.id }
            }

            Log.d("CONTAINERS_SERVICE", "Contenedores recibidos: ${containerList.size}")
            Result.success(containerList)
        } catch (e: Exception) {
            Log.e("CONTAINERS_SERVICE", "Error al obtener los contenedores", e)
            Result.failure(e)
        }
    }
}