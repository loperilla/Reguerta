package com.reguerta.domain.model.interfaces

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model.interfaces
 * Created By Manuel Lopera on 15/3/24 at 19:46
 * All rights reserved 2024
 */
interface Product {
    val id: String
    val container: String
    val description: String
    val name: String
    val price: Float
    val available: Boolean
    val companyName: String
    val imageUrl: String
    val stock: Int
    val quantityContainer: Int
    val quantityWeight: Int
    val unity: String
}