package com.reguerta.localdata.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reguerta.localdata.database.entity.OrderLineEntity
import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.database.dao
 * Created By Manuel Lopera on 13/3/24 at 18:39
 * All rights reserved 2024
 */
@Dao
interface OrderLineDao {
    @Query("SELECT * FROM OrderLineEntity WHERE userId = :userId AND week = :week AND orderId = :orderId")
    fun getOrderLinesByUserAndWeek(userId: String, week: Int, orderId: String): Flow<List<OrderLineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewOrderLine(orderLineEntity: OrderLineEntity): Long

    @Delete
    fun deleteOrder(orderLineEntity: OrderLineEntity)

    @Query("UPDATE OrderLineEntity SET quantity = :quantity WHERE userId = :userId AND week = :week AND orderId = :orderId AND productId = :productId")
    fun updateQuantity(userId: String, week: Int, orderId: String, productId: String, quantity: Int)
}