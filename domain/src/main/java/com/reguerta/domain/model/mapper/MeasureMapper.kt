package com.reguerta.domain.model.mapper

import com.reguerta.data.firebase.firestore.measures.MeasureService
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.toDomain
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model.mapper
 * Created By Manuel Lopera on 16/3/24 at 12:00
 * All rights reserved 2024
 */

class MeasureMapper @Inject constructor(
    private val measureService: MeasureService
) {
    suspend fun getSingleMeasure(name: String): Result<Measure> {
        return measureService.getMeasureByName(name).map { it.toDomain() }
    }
}
