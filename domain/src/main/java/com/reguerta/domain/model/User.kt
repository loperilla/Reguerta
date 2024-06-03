package com.reguerta.domain.model

import com.reguerta.data.firebase.firestore.users.UserModel

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 24/2/24 at 12:28
 * All rights reserved 2024
 */

data class User(
    val id: String,
    val email: String,
    val companyName: String,
    val isAdmin: Boolean,
    val isProducer: Boolean,
    val name: String,
    val surname: String,
    val phone: String,
    val numResignations: Int,
    val typeConsumer: String,
    val typeProducer: String,
    val available: Boolean
) {
    val fullName: String get() = "$name $surname"
}

fun UserModel.toDomain() = User(
    id = id.orEmpty(),
    email = email.orEmpty(),
    companyName = companyName.orEmpty(),
    isAdmin = isAdmin,
    isProducer = isProducer,
    name = name.orEmpty(),
    surname = surname.orEmpty(),
    phone = phone.orEmpty(),
    numResignations = numResignations ?: 0,
    typeConsumer = typeConsumer ?: "normal",
    typeProducer = typeProducer ?: "",
    available = available ?: true
)


enum class TypeConsumerUser(val type: String) {
    REGULAR("normal"),
    ODD("impar"),
    EVEN("par"),
    NONE("sin")
}

enum class TypeProducerUser(val type: String) {
    REGULAR("normal"),
    ODD("impar"),
    EVEN("par"),
    SHOP("compras")
}

