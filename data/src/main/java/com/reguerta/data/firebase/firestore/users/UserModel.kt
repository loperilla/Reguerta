package com.reguerta.data.firebase.firestore.users

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.users
 * Created By Manuel Lopera on 8/2/24 at 16:11
 * All rights reserved 2024
 */

data class UserModel(
    var id: String? = null,
    val email: String? = null,
    val companyName: String? = null,
    @field:JvmField val isAdmin: Boolean = false,
    @field:JvmField val isProducer: Boolean = false,
    val name: String? = null,
    val surname: String? = null,
    val phone: String? = null,
    var numResignations: Int? = null,
    val typeConsumer: String? = null,
    val typeProducer: String? = null,
    @field:JvmField var available: Boolean? = null
)

fun UserModel.toMapWithoutId(): Map<String, Any?> {
    return mapOf(
        "email" to email,
        "companyName" to companyName,
        "isAdmin" to isAdmin,
        "isProducer" to isProducer,
        "name" to name,
        "surname" to surname,
        "phone" to phone,
        "numResignations" to numResignations,
        "typeConsumer" to typeConsumer,
        "typeProducer" to typeProducer,
        "available" to available
    ).filterValues { it != null }
}
