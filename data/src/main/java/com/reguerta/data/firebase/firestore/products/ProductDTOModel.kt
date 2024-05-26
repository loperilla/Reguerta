package com.reguerta.data.firebase.firestore.products

import java.util.UUID

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.products
 * Created By Manuel Lopera on 3/3/24 at 13:10
 * All rights reserved 2024
 */

data class ProductDTOModel(
    val userId: String? = null,
    val name: String? = null,
    val container: String? = null,
    val description: String? = null,
    val price: Float? = null,
    val available: Boolean? = null,
    val companyName: String? = null,
    val urlImage: String? = null,
    val stock: Int? = null,
    val quantityContainer: Int? = null,
    val quantityWeight: Int? = null,
    val unity: String? = null
) {
    fun buildImageRef() = "$companyName/${name}_${UUID.randomUUID()}.jpg"
}
