package com.reguerta.domain.model

import com.reguerta.domain.model.interfaces.Product

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.model
 * Created By Manuel Lopera on 25/2/24 at 11:25
 * All rights reserved 2024
 */
data class CommonProduct(
    override val id: String = "",
    override val container: String = "",
    override val description: String = "",
    override val name: String = "",
    override val price: Float = 0.0f,
    override val available: Boolean = false,
    override val companyName: String = "",
    override val imageUrl: String = "",
    override val stock: Int = 0,
    override val quantityContainer: Int = 0,
    override val quantityWeight: Int = 0,
    override val unity: String = "",
) : Product