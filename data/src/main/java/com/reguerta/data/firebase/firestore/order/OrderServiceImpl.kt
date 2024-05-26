package com.reguerta.data.firebase.firestore.order

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.USER_ID
import com.reguerta.data.firebase.firestore.WEEK
import com.reguerta.data.firebase.model.DataError
import com.reguerta.data.firebase.model.DataResult
import com.reguerta.localdata.datastore.NAME_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.SURNAME_KEY
import com.reguerta.localdata.datastore.UID_KEY
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.order
 * Created By Manuel Lopera on 13/3/24 at 19:11
 * All rights reserved 2024
 */
class OrderServiceImpl @Inject constructor(
    private val collection: CollectionReference,
    private val dataStore: ReguertaDataStore,
    private val weekTime: WeekTime
) : OrderServices {
    override suspend fun getOrderByUserId(): DataResult<OrderModel, DataError.Firebase> {
        return try {
            val snapshot = collection
                .whereEqualTo(
                    USER_ID,
                    dataStore.getStringByKey(UID_KEY)
                )
                .whereEqualTo(
                    WEEK,
                    weekTime.getCurrentWeek()
                )
                .get()
                .await()
            val document = snapshot.documents.firstOrNull() ?: return insertDefaultModel()
            val orderModel = document.toObject(OrderModel::class.java)!!
            orderModel.orderId = document.id
            DataResult.Success(orderModel)
        } catch (ex: Exception) {
            DataResult.Error(DataError.Firebase.UNKNOWN)
        }
    }

    override suspend fun getOrderByUserId(userId: String): DataResult<OrderModel, DataError.Firebase> {
        return try {
            val snapshot = collection
                .whereEqualTo(USER_ID, userId)
                .whereEqualTo(WEEK, weekTime.getCurrentWeek().minus(1))
                .get()
                .await()
            if (snapshot.documents.isEmpty()) {
                return DataResult.Error(DataError.Firebase.EMPTY_LIST)
            }
            val document = snapshot.documents.first()
            val orderModel = document.toObject(OrderModel::class.java)!!
            orderModel.orderId = document.id
            DataResult.Success(orderModel)
        } catch (ex: Exception) {
            DataResult.Error(DataError.Firebase.UNKNOWN)
        }
    }
/*
    private suspend fun insertDefaultModel(): DataResult<OrderModel, DataError.Firebase> {
        return try {
            val orderModelDefault = OrderModel(
                week = weekTime.getCurrentWeek(),
                userId = dataStore.getStringByKey(UID_KEY),
                name = dataStore.getStringByKey(NAME_KEY),
                surname = dataStore.getStringByKey(SURNAME_KEY)
            )
            collection
                .add(orderModelDefault.toMapWithoutId())
                .await()
            DataResult.Success(orderModelDefault)
        } catch (ex: Exception) {
            DataResult.Error(DataError.Firebase.UNKNOWN)
        }
    }
*/
    private suspend fun insertDefaultModel(): DataResult<OrderModel, DataError.Firebase> {
        return try {
            val orderModelDefault = OrderModel(
                week = weekTime.getCurrentWeek(),
                userId = dataStore.getStringByKey(UID_KEY),
                name = dataStore.getStringByKey(NAME_KEY),
                surname = dataStore.getStringByKey(SURNAME_KEY)
            )
            val documentReference = collection
                .add(orderModelDefault.toMapWithoutId())
                .await()
            orderModelDefault.orderId = documentReference.id
            DataResult.Success(orderModelDefault)
        } catch (ex: Exception) {
            DataResult.Error(DataError.Firebase.UNKNOWN)
        }
    }


    override suspend fun deleteOrder(orderId: String): DataResult<Unit, DataError.Firebase> {
        return try {
            collection
                .document(orderId)
                .delete()
                .await()
            DataResult.Success(Unit)
        } catch (ex: Exception) {
            DataResult.Error(DataError.Firebase.UNKNOWN)
        }
    }
}