package com.reguerta.presentation.screen.new_order

import com.reguerta.domain.model.Container
import com.reguerta.domain.model.Measure
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.model.OrderLineReceived
import java.time.DayOfWeek

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */

data class NewOrderState(
    val goOut: Boolean = false,
    val isLoading: Boolean = true,
    val isExistOrder: Boolean = false,
    val hasOrderLine: Boolean = false,
    val showShoppingCart: Boolean = false,
    val showPopup: PopupType = PopupType.NONE,
    val errorMessage: String? = null,
    val availableCommonProducts: List<Product> = emptyList(),
    val productsOrderLineList: List<ProductWithOrderLine> = emptyList(),
    val ordersFromExistingOrder: Map<Product, List<OrderLineReceived>> = emptyMap(),
    val orderLinesByCompanyName: Map<String, List<OrderLineReceived>> = emptyMap(),
    val productsGroupedByCompany: Map<String, List<Product>> = emptyMap(),
    val currentDay: DayOfWeek = DayOfWeek.MONDAY,
    val measures: List<Measure> = emptyList(),
    val containers: List<Container> = emptyList(),
    val kgMangoes: Int = 0,
    val kgAvocados: Int = 0
)

enum class PopupType {
    NONE,
    ARE_YOU_SURE_DELETE,
    ORDER_ADDED,
    MISSING_COMMIT
}
