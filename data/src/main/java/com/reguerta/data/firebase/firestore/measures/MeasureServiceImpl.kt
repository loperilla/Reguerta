package com.reguerta.data.firebase.firestore.measures

import com.google.firebase.firestore.CollectionReference
import com.reguerta.localdata.database.dao.MeasureDao
import com.reguerta.localdata.database.entity.MeasureEntity
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
    private val collection: CollectionReference,
    private val measureDao: MeasureDao
) : MeasureService {
    override suspend fun getMeasures(): Flow<Result<List<MeasureModel>>> = callbackFlow {
        val persistedMeasure = measureDao.getAllMeasures()
        if (persistedMeasure.isNotEmpty()) {
            trySend(Result.success(persistedMeasure.toModel()))
        } else {
            getNetworkMeasures().collect { result ->
                result.onSuccess { measureList ->
                    measureDao.insertAllMeasures(measureList.toEntity())
                    trySend(Result.success(measureList))
                }
                result.onFailure {
                    trySend(Result.failure(it))
                }
            }
        }
    }

    override suspend fun getMeasureByName(name: String): Result<MeasureModel> {
        return try {
            Result.success(measureDao.getMeasureByName(name).toModel())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

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
}