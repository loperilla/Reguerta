package com.reguerta.data.firebase.firestore.measures

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.measures
 * Created By Manuel Lopera on 2/3/24 at 12:31
 * All rights reserved 2024
 */
class MeasureServiceImpl @Inject constructor(
    private val collection: CollectionReference
) : MeasureService {
    override suspend fun getMeasures(): Flow<Result<List<MeasureModel>>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                trySend(Result.failure(error))
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let { query ->
                val measureList = mutableListOf<MeasureModel>()
                query.documents.forEach { document ->
                    val measureModel = document.toObject(MeasureModel::class.java)
                    measureModel?.let { model ->
                        model.id = document.id
                        measureList.add(model)
                    }
                }
                trySend(Result.success(measureList))
            }
        }
        awaitClose {
            subscription.remove()
        }
    }
}
