package com.reguerta.data.firebase.firestore.order

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.order
 * Created By Manuel Lopera on 13/3/24 at 19:08
 * All rights reserved 2024
 */
data class OrderModel(
    var orderId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val week: Int? = null
) {
    fun toMapWithoutId(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "week" to week
        )
    }
}