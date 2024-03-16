package com.reguerta.localdata.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reguerta.localdata.database.dao.MeasureDao
import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.database.entity.MeasureEntity
import com.reguerta.localdata.database.entity.OrderLineEntity

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.database
 * Created By Manuel Lopera on 13/3/24 at 18:36
 * All rights reserved 2024
 */
@Database(
    entities = [
        OrderLineEntity::class,
        MeasureEntity::class
    ],
    version = 1
)
abstract class ReguertaDatabase : RoomDatabase() {
    abstract fun orderLineDao(): OrderLineDao
    abstract fun measureDao(): MeasureDao
}
