package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.measures.MeasureModel

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 2/3/24 at 12:36
 * All rights reserved 2024
 */
data class Measure(
    val id: String,
    val abbreviation: String,
    val name: String,
    val plural: String,
    val type: String
)

fun MeasureModel.toDomain() = Measure(
    id = id.orEmpty(),
    abbreviation = abbreviation.orEmpty(),
    name = name.orEmpty(),
    plural = plural.orEmpty(),
    type = type.orEmpty()
)