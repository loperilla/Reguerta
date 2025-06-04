package com.reguerta.domain.usecase.measures

import com.reguerta.data.firebase.firestore.measures.MeasuresService
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.measures
 * Created By Manuel Lopera on 2/3/24 at 12:36
 * All rights reserved 2024
 */

class GetAllMeasuresUseCase @Inject constructor(
    private val measuresService: MeasuresService
) {
    suspend operator fun invoke(): List<Measure> {
        val start = System.currentTimeMillis()
        val result = measuresService.getAllMeasures()
        val elapsed = System.currentTimeMillis() - start
        println("SYNC_ \uD83D\uDD25 getAllMeasuresUseCase tardó $elapsed ms")

        return result.fold(
            onSuccess = { list -> list.map { it.toDomain() } },
            onFailure = { emptyList() }
        )
    }
}