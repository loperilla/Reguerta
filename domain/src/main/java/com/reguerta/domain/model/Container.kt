package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.containers.ContainerModel

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 2/3/24 at 12:51
 * All rights reserved 2024
 */

data class Container(
    val id: String,
    val name: String,
    val plural: String
)

fun ContainerModel.toDomain() = Container(
    id = id.orEmpty(),
    name = name.orEmpty(),
    plural = plural.orEmpty()
)
    