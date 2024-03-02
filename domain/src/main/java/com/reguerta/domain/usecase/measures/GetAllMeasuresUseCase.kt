package com.reguerta.domain.usecase.measures

import com.reguerta.data.firebase.firestore.measures.MeasureService
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
    private val measureService: MeasureService
) {

    suspend operator fun invoke(): Flow<List<Measure>> {
        return measureService.getMeasures().map {
            it.fold(
                onSuccess = { measureModelList ->
                    measureModelList.map { measureModel ->
                        measureModel.toDomain()
                    }
                },
                onFailure = {
                    emptyList()
                }
            )
        }
    }
}