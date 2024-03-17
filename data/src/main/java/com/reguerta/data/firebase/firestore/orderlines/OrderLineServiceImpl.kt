package com.reguerta.data.firebase.firestore.orderlines

import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.database.entity.OrderLineEntity
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.UID_KEY
import com.reguerta.localdata.time.WeekTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.orderlines
 * Created By Manuel Lopera on 15/3/24 at 19:14
 * All rights reserved 2024
 */
class OrderLineServiceImpl @Inject constructor(
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

    override suspend fun addOrderLine(orderId: String, productId: String) {
        dao.insertNewOrderLine(
            OrderLineEntity(
                orderId = orderId,
                userId = dataStore.getStringByKey(UID_KEY),
                productId = productId,
                quantity = 1,
                week = time.getCurrentWeek()
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
}