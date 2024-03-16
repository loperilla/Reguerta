package com.reguerta.data.firebase.firestore.order

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.USER_ID
import com.reguerta.data.firebase.firestore.WEEK
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
    override suspend fun getOrderByUserId(): Result<OrderModel> {
        return try {
            val snapshot = collection
                .whereEqualTo(USER_ID, dataStore.getStringByKey(UID_KEY))
                .whereEqualTo(WEEK, weekTime.getCurrentWeek())
                .get()
                .await()
            val document = snapshot.documents.first()
            val orderModel = document.toObject(OrderModel::class.java) ?: return insertDefaultModel()
            orderModel.orderId = document.id
            Result.success(orderModel)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    private suspend fun insertDefaultModel(): Result<OrderModel> {
        return try {
            val orderModelDefault = OrderModel(
                week = weekTime.getCurrentWeek(),
                userId = dataStore.getStringByKey(UID_KEY),
                name = dataStore.getStringByKey(NAME_KEY),
                surname = dataStore.getStringByKey(SURNAME_KEY)
            )
            collection
                .add(orderModelDefault)
                .await()
            Result.success(orderModelDefault)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}