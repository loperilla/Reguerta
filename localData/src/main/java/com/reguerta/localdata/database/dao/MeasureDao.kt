package com.reguerta.localdata.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.reguerta.localdata.database.entity.MeasureEntity

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.database.dao
 * Created By Manuel Lopera on 16/3/24 at 11:47
 * All rights reserved 2024
 */

@Dao
interface MeasureDao {
    @Insert
    fun insertAllMeasures(measures: List<MeasureEntity>)

    @Query("SELECT * FROM MeasureEntity")
    fun getAllMeasures(): List<MeasureEntity>

    @Query("SELECT * FROM MeasureEntity WHERE name = :name")
    fun getMeasureByName(name: String): MeasureEntity
}