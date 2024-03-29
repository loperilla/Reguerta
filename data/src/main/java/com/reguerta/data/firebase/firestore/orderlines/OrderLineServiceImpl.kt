package com.reguerta.data.firebase.firestore.orderlines

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.COMPANY_NAME
import com.reguerta.data.firebase.firestore.WEEK
import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.database.entity.OrderLineEntity
import com.reguerta.localdata.datastore.COMPANY_NAME_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.UID_KEY
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 15/3/24 at 19:14
 * All rights reserved 2024
 */
class OrderLineServiceImpl @Inject constructor(
    private val collection: CollectionReference,
    private val dao: OrderLineDao,
    private val time: WeekTime,
    private val dataStore: ReguertaDataStore
) : OrderLineService {
    override suspend fun getOrderLines(orderId: String): Flow<List<OrderLineDTO>> = dao.getOrderLinesByUserAndWeek(
        dataStore.getStringByKey(UID_KEY),
        time.getCurrentWeek(),
        orderId
    ).map { list ->
        list.map { entity -> entity.toDTO() }
    }

    override suspend fun addOrderLineInDatabase(orderId: String, productId: String, productCompany: String) {
        dao.insertNewOrderLine(
            OrderLineEntity(
                orderId = orderId,
                userId = dataStore.getStringByKey(UID_KEY),
                productId = productId,
                quantity = 1,
                week = time.getCurrentWeek(),
                companyName = productCompany
            )
        )
    }

    override suspend fun updateQuantity(orderId: String, productId: String, quantity: Int) {
        dao.updateQuantity(
            dataStore.getStringByKey(UID_KEY),
            time.getCurrentWeek(),
            orderId,
            productId,
            quantity
        )
    }

    override suspend fun deleteOrderLine(orderId: String, productId: String) {
        dao.deleteOrder(
            dataStore.getStringByKey(UID_KEY),
            time.getCurrentWeek(),
            orderId,
            productId
        )
    }

    override suspend fun addOrderLineInFirebase(listToPush: List<OrderLineDTO>): Result<Unit> {
        return try {
            listToPush.forEach {
                collection
                    .add(it)
                    .await()
            }
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun getOrdersByCompanyAndWeek(): Flow<Result<List<OrderLineModel>>> = callbackFlow {
        val subscription = collection
            .whereEqualTo(
                COMPANY_NAME,
                dataStore.getStringByKey(COMPANY_NAME_KEY)
            ).whereEqualTo(
                WEEK,
                time.getCurrentWeek().minus(1)
            )
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    trySend(Result.failure(error))
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { query ->
                    val orderLineList = mutableListOf<OrderLineModel>()
                    query.documents.forEach { document ->
                        val orderLine = document.toObject(OrderLineModel::class.java)
                        orderLine?.let { model ->
                            model.id = document.id
                            orderLineList.add(model)
                        }
                    }
                    trySend(Result.success(orderLineList))
                }
            }
        awaitClose {
            subscription.remove()
        }
    }
}