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
    val surname: String? = null
)
