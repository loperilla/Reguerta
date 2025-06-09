package com.reguerta.data.firebase.firestore.orderlines

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.COMPANY_NAME
import com.reguerta.data.firebase.firestore.ORDER_ID
import com.reguerta.data.firebase.firestore.WEEK
import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.database.entity.OrderLineEntity
import com.reguerta.localdata.datastore.COMPANY_NAME_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.UID_KEY
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 15/3/24 at 19:14
 * All rights reserved 2024
 */

class OrderLinesServiceImpl @Inject constructor(
    private val collection: CollectionReference,
    private val dao: OrderLineDao,
    private val time: WeekTime,
    private val dataStore: ReguertaDataStore
) : OrderLinesService {
    override suspend fun getOrderLines(orderId: String): Flow<List<OrderLineDTO>> = withContext(Dispatchers.IO) {
        dao.getOrderLinesByUserAndWeek(
            dataStore.getStringByKey(UID_KEY),
            time.getCurrentWeek(),
            orderId
        ).map { list ->
            list.map { entity -> entity.toDTO() }
        }
    }

    override suspend fun addOrderLineInDatabase(orderId: String, productId: String, productCompany: String) {
        withContext(Dispatchers.IO) {
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
    }

    override suspend fun updateQuantity(orderId: String, productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        dao.updateQuantity(
            dataStore.getStringByKey(UID_KEY),
            time.getCurrentWeek(),
            orderId,
            productId,
            quantity
        )
    }

    override suspend fun deleteOrderLine(orderId: String, productId: String) = withContext(Dispatchers.IO) {
        dao.deleteOrder(
            dataStore.getStringByKey(UID_KEY),
            time.getCurrentWeek(),
            orderId,
            productId
        )
    }

    override suspend fun deleteFirebaseOrderLine(orderId: String) {
        collection
            .whereEqualTo(ORDER_ID, orderId)
            .get()
            .addOnSuccessListener {
                it.documents.forEach { document ->
                    document.reference.delete()
                }
            }
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
        // HACK para pruebas: forzamos el companyName al de un productor real con pedidos recibidos
        //val companyName = "El Laurel de Cantillo"
        // Para producción, volver a dejar:
        val companyName = withContext(Dispatchers.IO) { dataStore.getStringByKey(COMPANY_NAME_KEY) }
        val subscription = collection
            .whereEqualTo(COMPANY_NAME, companyName)
            .whereEqualTo(WEEK, time.getCurrentWeek().minus(1))
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

    override suspend fun getOrdersByOrderId(orderId: String): Flow<Result<List<OrderLineModel>>> = callbackFlow {
        val subscription = collection
            .whereEqualTo(ORDER_ID, orderId)
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

    override suspend fun checkIfExistOrderInFirebase(orderId: String): Result<Boolean> {
        return try {
            val result = collection
                .whereEqualTo(ORDER_ID, orderId)
                .get()
                .await()
            Result.success(result.documents.isNotEmpty())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun getAllOrderLinesByOrderId(orderId: String): Result<List<OrderLineModel>> {
        return try {
            val snapshot = collection
                .whereEqualTo(ORDER_ID, orderId)
                .get()
                .await()

            val orderLines = snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderLineModel::class.java)?.apply { id = doc.id }
            }

            Log.d("ORDERLINES_SERVICE", "OrderLines del pedido $orderId: ${orderLines.size}")
            Result.success(orderLines)
        } catch (e: Exception) {
            Log.e("ORDERLINES_SERVICE", "Error al obtener orderLines para $orderId", e)
            Result.failure(e)
        }
    }
}