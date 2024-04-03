package com.reguerta.localdata.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.database.entity
 * Created By Manuel Lopera on 16/3/24 at 11:46
 * All rights reserved 2024
 */
@Entity
data class MeasureEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    val abbreviation: String,
    val name: String,
    val plural: String,
    val type: String
)
