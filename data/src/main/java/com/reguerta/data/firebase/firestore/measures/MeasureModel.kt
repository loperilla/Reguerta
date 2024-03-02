package com.reguerta.data.firebase.firestore.measures

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.measures
 * Created By Manuel Lopera on 2/3/24 at 12:30
 * All rights reserved 2024
 */
data class MeasureModel(
    var id: String? = null,
    val abbreviation: String? = null,
    val name: String? = null,
    val plural: String? = null,
    val type: String? = null,
)
