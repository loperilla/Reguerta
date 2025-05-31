package com.reguerta.data.firebase.firestore.measures

import com.google.firebase.firestore.CollectionReference
import com.reguerta.localdata.database.dao.MeasureDao
import com.reguerta.localdata.database.entity.MeasureEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.measures
 * Created By Manuel Lopera on 2/3/24 at 12:31
 * All rights reserved 2024
 */

class MeasuresServiceImpl @Inject constructor(
    private val collection: CollectionReference,
    private val measureDao: MeasureDao
) : MeasuresService {
    override suspend fun getMeasures(): Flow<Result<List<MeasureModel>>> = callbackFlow {
        val persistedMeasure = measureDao.getAllMeasures()
        if (persistedMeasure.isNotEmpty()) {
            trySend(Result.success(persistedMeasure.toModel()))
            close() // Cerramos directamente el flujo ya que no hay escucha activa de snapshot
            return@callbackFlow
        }

        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                close(error)
                return@addSnapshotListener
            }

            val result = snapshot?.documents?.mapNotNull { document ->
                document.toObject(MeasureModel::class.java)?.apply {
                    id = document.id
                }
            } ?: emptyList()

            if (result.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    measureDao.insertAllMeasures(result.toEntity())
                }
                trySend(Result.success(result))
            } else {
                trySend(Result.success(emptyList()))
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getMeasureByName(name: String): Result<MeasureModel> {
        return try {
            withContext(Dispatchers.IO) {
                Result.success(measureDao.getMeasureByName(name).toModel())
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
/*
    private suspend fun getNetworkMeasures(): Flow<Result<List<MeasureModel>>> = callbackFlow {
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
 */

    private fun List<MeasureEntity>.toModel() = map {
        MeasureModel(
            abbreviation = it.abbreviation,
            name = it.name,
            plural = it.plural,
            type = it.type
        )
    }

    private fun MeasureEntity.toModel() = MeasureModel(
        abbreviation = abbreviation,
        name = name,
        plural = plural,
        type = type
    )

    private fun List<MeasureModel>.toEntity() = map {
        MeasureEntity(
            abbreviation = it.abbreviation.orEmpty(),
            name = it.name.orEmpty(),
            plural = it.plural.orEmpty(),
            type = it.type.orEmpty()
        )
    }

    override suspend fun getAllMeasures(): Result<List<MeasureModel>> {
        return try {
            val snapshot = collection
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val measures = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MeasureModel::class.java)?.apply { id = doc.id }
            }

            // Opcional: persistir en base de datos local
            if (measures.isNotEmpty()) {
                measureDao.insertAllMeasures(measures.toEntity())
            }

            Log.d("MEASURES_SERVICE", "Medidas recibidas: ${measures.size}")
            Result.success(measures)
        } catch (e: Exception) {
            Log.e("MEASURES_SERVICE", "Error al obtener las medidas", e)
            Result.failure(e)
        }
    }
}