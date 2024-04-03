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
    val phone: String
) {
    val fullName = this.name + " " + this.surname
}

fun UserModel.toDomain() = User(
    id = id.orEmpty(),
    email = email.orEmpty(),
    companyName = companyName.orEmpty(),
    isAdmin = isAdmin,
    isProducer = isProducer,
    name = name.orEmpty(),
    surname = surname.orEmpty(),
    phone = phone.orEmpty()
)