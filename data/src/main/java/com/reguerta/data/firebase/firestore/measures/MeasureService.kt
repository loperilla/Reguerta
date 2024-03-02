package com.reguerta.data.firebase.firestore.measures

import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.measures
 * Created By Manuel Lopera on 2/3/24 at 12:30
 * All rights reserved 2024
 */
interface MeasureService {
    suspend fun getMeasures(): Flow<Result<List<MeasureModel>>>
}
