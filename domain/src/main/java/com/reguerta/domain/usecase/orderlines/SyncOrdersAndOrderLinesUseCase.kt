package com.reguerta.domain.usecase.orderlines

import android.util.Log
import com.google.firebase.Timestamp
import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.localdata.datastore.ReguertaDataStore
import javax.inject.Inject

class SyncOrdersAndOrderLinesUseCase @Inject constructor(
    private val ordersService: OrdersService,
    private val orderLinesService: OrderLinesService,
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(remoteTimestamp: Timestamp) {
        Log.d("SYNC_OrdersUseCase", "Iniciando sincronización de pedidos y líneas de pedido con timestamp: ${remoteTimestamp.seconds}")
        val result = ordersService.getAllOrders()
        result.onSuccess { orders ->
            // Por cada pedido, cargamos sus líneas
            orders.forEach { order ->
                orderLinesService.getAllOrderLinesByOrderId(order.id)
                // TODO: Guardar las líneas si es necesario
            }
            // TODO: Guardar los pedidos si aplica
            Log.d("SYNC_OrdersUseCase", "Sincronización de pedidos y líneas completada correctamente.")
            dataStore.saveSyncTimestamp("orders", remoteTimestamp.seconds)
        }.onFailure {
            Log.e("SYNC_OrdersUseCase", "Error al sincronizar pedidos y líneas de pedido", it)
        }
    }
}