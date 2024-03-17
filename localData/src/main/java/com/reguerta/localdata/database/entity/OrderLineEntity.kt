package com.reguerta.localdata.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.database.entity
 * Created By Manuel Lopera on 13/3/24 at 18:37
 * All rights reserved 2024
 */
@Entity
data class OrderLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val orderId: String,
    val productId: String,
    val companyName: String,
    val userId: String,
    val quantity: Int,
    val week: Int
)
