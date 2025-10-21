package com.reguerta.domain.usecase.orderlines

import com.google.firebase.Timestamp
import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.localdata.datastore.ReguertaDataStore
import timber.log.Timber
import javax.inject.Inject

class SyncOrdersAndOrderLinesUseCase @Inject constructor(
    private val ordersService: OrdersService,
    private val orderLinesService: OrderLinesService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Timber.tag("SYNC_OrdersUseCase").d("Iniciando sincronización de pedidos y líneas de pedido con timestamp: ${remoteTimestamp.seconds}")

        val ordersResult = ordersService.getAllOrders()
        ordersResult.onFailure {
            Timber.tag("SYNC_OrdersUseCase").e(it, "Error al sincronizar pedidos")
            return
        }

        val orders = ordersResult.getOrNull().orEmpty()
        var allOk = true

        // Cargamos líneas por pedido de forma secuencial para máxima estabilidad (sin concurrencia).
        for (order in orders) {
            val res = runCatching { orderLinesService.getAllOrderLinesByOrderId(order.id) }
            res.onFailure { e ->
                allOk = false
                Timber.tag("SYNC_OrdersUseCase").e(e, "Error al sincronizar líneas del pedido ${order.id}")
            }
        }

        if (allOk) {
            dataStore.saveSyncTimestamp("orders", remoteTimestamp.seconds)       // segundos
            dataStore.saveSyncTimestamp("orderlines", remoteTimestamp.seconds)  // espejo para clave remota
            Timber.tag("SYNC_OrdersUseCase").d("Sincronización de pedidos y líneas completada correctamente.")
        } else {
            Timber.tag("SYNC_OrdersUseCase").w("Algunas líneas de pedido fallaron; no se actualizan timestamps (orders/orderlines).")
        }
    }
}