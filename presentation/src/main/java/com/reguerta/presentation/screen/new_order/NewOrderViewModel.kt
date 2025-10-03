package com.reguerta.presentation.screen.new_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.enums.toJavaDayOfWeek
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.NewOrderModel
import com.reguerta.domain.model.interfaces.Product
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.containers.GetAllContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.MapOrderLinesWithProductsUseCase
import com.reguerta.domain.usecase.products.CheckCommitmentsUseCase
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
import com.reguerta.domain.usecase.products.UpdateProductStockUseCase
import com.reguerta.domain.usecase.config.UpdateTableTimestampsUseCase
import com.reguerta.domain.usecase.config.GetDeliveryDayUseCase
import com.reguerta.domain.usecase.week.GetCurrentWeekDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.text.Normalizer
import java.time.DayOfWeek
import javax.inject.Inject
import kotlin.math.roundToInt

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.new_order
 * Created By Manuel Lopera on 10/3/24 at 12:12
 * All rights reserved 2024
 */

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val getAvailableProductsUseCase: GetAvailableProductsUseCase,
    private val getCurrentWeek: GetCurrentWeekDayUseCase,
    private val getAllMeasuresUseCase: GetAllMeasuresUseCase,
    private val getAllContainersUseCase: GetAllContainersUseCase,
    private val orderModel: NewOrderModel,
    private val updateProductStockUseCase: UpdateProductStockUseCase,
    private val checkCommitmentsUseCase: CheckCommitmentsUseCase,
    private val mapOrderLinesWithProductsUseCase: MapOrderLinesWithProductsUseCase,
    private val checkCurrentUserLoggedUseCase: CheckCurrentUserLoggedUseCase,
    private val updateTableTimestampsUseCase: UpdateTableTimestampsUseCase,
    private val getDeliveryDayUseCase: GetDeliveryDayUseCase,
) : ViewModel() {
    private var _state: MutableStateFlow<NewOrderState> = MutableStateFlow(NewOrderState())
    val state: StateFlow<NewOrderState> = _state.asStateFlow()

    private lateinit var initialCommonProducts: List<CommonProduct>

    private var hasForcedReload = false
    private var fallbackJob: Job? = null
    private var searchJob: Job? = null
    private val reloadMutex = Mutex()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            determineAndExecuteFlow()
            startFallbackReloads()
            viewModelScope.launch {
                state
                    .map { it.uiState }
                    .distinctUntilChanged()
                    .onEach { ui ->
                        if (ui != NewOrderUiMode.LOADING) {
                            fallbackJob?.cancel()
                        }
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    // Determina el flujo principal según el día y el usuario autenticado
    private suspend fun determineAndExecuteFlow() {
        Timber.i("SYNC_SYNC_INIT - Entrando en determineAndExecuteFlow()")
        _state.update { it.copy(uiState = NewOrderUiMode.LOADING) }
        val today = DayOfWeek.of(getCurrentWeek())
        val deliveryDay = getDeliveryDayUseCase()
        val isNewOrder = isNewOrderBranch(today, deliveryDay)
        Timber.i("SYNC_FLOW_BRANCH - Día actual: $today, deliveryDay: $deliveryDay, isNewOrderBranch: $isNewOrder")

        val fixedFlow = if (isNewOrder) OrderFlow.CURRENT_WEEK else OrderFlow.LAST_WEEK
        _state.update { it.copy(currentDay = today, flow = fixedFlow) }

        val currentUserResult = checkCurrentUserLoggedUseCase()
        val currentUser = currentUserResult.getOrNull()
        if (currentUser == null) {
            Timber.w("SYNC_SYNC_INIT - currentUser es null, abortando flujo")
            _state.update {
                it.copy(
                    uiState = NewOrderUiMode.ERROR,
                    errorMessage = "Usuario no autenticado"
                )
            }
            return
        }

        _state.update {
            it.copy(
                kgMangoes = currentUser.tropical1.roundToInt(),
                kgAvocados = currentUser.tropical2.roundToInt()
            )
        }

        if (isNewOrder) {
            Timber.i("SYNC_SYNC_FLOW: Ejecutando flujo de NUEVO PEDIDO (current week orders)")
            handleCurrentWeekOrders()
        } else {
            Timber.i("SYNC_SYNC_FLOW: Ejecutando flujo de PEDIDO ANTERIOR (last week orders)")
            handleLastWeekOrders()
        }
    }

    private fun startFallbackReloads() {
        fallbackJob?.cancel()
        fallbackJob = viewModelScope.launch {
            // 1er chequeo a los 5s
            delay(5_000)
            if (state.value.uiState == NewOrderUiMode.LOADING) {
                // solo si no hay otro reload en curso
                if (!reloadMutex.isLocked) {
                    reloadMutex.withLock {
                        if (state.value.uiState == NewOrderUiMode.LOADING) {
                            forceReloadOnce()
                        }
                    }
                }
                // 2º chequeo a los 10s totales
                delay(5_000)
                if (state.value.uiState == NewOrderUiMode.LOADING) {
                    if (!reloadMutex.isLocked) {
                        reloadMutex.withLock {
                            if (state.value.uiState == NewOrderUiMode.LOADING) {
                                forceReloadOnce()
                            }
                        }
                    }
                }
            }
        }
    }


    // Carga los productos disponibles, reintenta si es necesario
    private suspend fun loadAvailableProducts(getAvailableProductsUseCase: GetAvailableProductsUseCase): Boolean {
        Timber.i("SYNC_SYNC_FORCE_RELOAD - Ejecutando loadAvailableProducts()")
        var intentos = 0
        val maxIntentos = 10
        val delayMillis = 300L
        while (intentos < maxIntentos) {
            try {
                Timber.i("SYNC_LOAD_PRODUCTS - Intento $intentos: Llamando a getAvailableProductsUseCase()")
                val availableProducts = getAvailableProductsUseCase().first()
                Timber.i("SYNC_LOAD_PRODUCTS - Productos obtenidos (${availableProducts.size}): $availableProducts")
                if (availableProducts.isNotEmpty()) {
                    if (::initialCommonProducts.isInitialized && initialCommonProducts == availableProducts) {
                        return true
                    }
                    initialCommonProducts = availableProducts
                    Timber.i("SYNC_INIT_COMMON_PRODUCTS - Productos inicializados (${initialCommonProducts.size}): $initialCommonProducts")
                    applySearchFilter()
                    Timber.i("SYNC_Productos recargados: ${availableProducts.size}")
                    return true
                }
            } catch (e: Exception) {
                Timber.e(e, "SYNC_ERROR_LOAD_PRODUCTS - ${e.message}")
                Timber.e(e, "SYNC_Error al cargar productos disponibles: ${e.message}")
                handleError(e)
                Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
                _state.update {
                    it.copy(
                        uiState = NewOrderUiMode.ERROR,
                        errorMessage = e.message ?: "No se pudieron cargar productos disponibles"
                    )
                }
                return false
            }
            intentos++
            delay(delayMillis)
        }
        Timber.e("SYNC_LOAD_PRODUCTS - No se pudieron obtener productos tras $maxIntentos intentos")
        handleError(IllegalStateException("No se pudieron obtener productos tras $maxIntentos intentos"))
        Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
        _state.update {
            it.copy(
                uiState = NewOrderUiMode.ERROR,
                errorMessage = "No se pudieron obtener productos tras $maxIntentos intentos"
            )
        }
        return false
    }

    // Maneja el flujo de pedidos de la semana pasada
    private suspend fun handleLastWeekOrders() {
        Timber.i("SYNC_SYNC_handleLastWeekOrders: comprobando si existe pedido anterior...")
        orderModel.checkIfExistLastWeekOrderInFirebase().fold(
            onSuccess = { existOrder ->
                if (existOrder) {
                    observeOrderLines(isEdit = false)
                } else {
                    Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
                    _state.update {
                        it.copy(
                            uiState = NewOrderUiMode.ERROR,
                            errorMessage = "No hay pedido anterior disponible"
                        )
                    }
                    Timber.i("SYNC_SYNC_FLOW_TYPE - No hay pedido anterior, se mostrará pantalla vacía (LastOrderScreen sin datos)")
                }
            },
            onFailure = {
                Timber.e(it, "SYNC_ERROR_HANDLE_ORDER - ${it.message}")
                Timber.e(it, "SYNC_SYNC_handleLastWeekOrders - onFailure: ${it.message}")
                handleError(it)
                Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
                _state.update { orderState ->
                    orderState.copy(
                        uiState = NewOrderUiMode.ERROR,
                        errorMessage = orderState.errorMessage ?: "Error desconocido"
                    )
                }
            }
        )
    }

    // Maneja el flujo de pedidos de la semana actual
    private suspend fun handleCurrentWeekOrders() {
        Timber.i("SYNC_SYNC_handleCurrentWeekOrders: comprobando si existe pedido actual...")
        orderModel.checkIfExistOrderInFirebase().fold(
            onSuccess = { existOrder ->
                if (existOrder) {
                    observeOrderLines(isEdit = true)
                } else {
                    Timber.i("SYNC_SYNC_handleCurrentWeekOrders - No hay pedido actual, no se ejecuta loadNewOrderLines() para no sobreescribir productos disponibles.")
                    val success = loadAvailableProducts(getAvailableProductsUseCase)
                    if (success) {
                        _state.update { it.copy(uiState = NewOrderUiMode.SELECT_PRODUCTS) }
                    } else {
                        Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
                        _state.update {
                            it.copy(
                                uiState = NewOrderUiMode.ERROR,
                                errorMessage = "No se pudieron cargar productos disponibles"
                            )
                        }
                        Timber.w("SYNC_SYNC_WARNING - No se pudieron cargar productos disponibles correctamente")
                    }
                }
            },
            onFailure = {
                Timber.e(it, "SYNC_ERROR_HANDLE_ORDER - ${it.message}")
                Timber.e(it, "SYNC_SYNC_handleCurrentWeekOrders - onFailure: ${it.message}")
                handleError(it)
                Timber.e("SYNC_UI_STATE - Cambiando a ERROR. Estado actual: $_state")
                _state.update { orderState ->
                    orderState.copy(
                        uiState = NewOrderUiMode.ERROR,
                        errorMessage = orderState.errorMessage ?: "Error desconocido"
                    )
                }
            }
        )
    }

    // Maneja errores y actualiza el estado de error
    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "SYNC_ERROR_HANDLE - ${throwable.message}")
        _state.update {
            it.copy(
                uiState = NewOrderUiMode.ERROR,
                errorMessage = throwable.message ?: "Error desconocido"
            )
        }
        throwable.printStackTrace()
    }


    // Helper to normalize strings for diacritics-insensitive search
    private fun String.foldSearch(): String {
        val nfd = Normalizer.normalize(this, Normalizer.Form.NFD)
        // Remove all combining marks (accents/diacritics) and lowercase
        return nfd.replace(Regex("\\p{M}+"), "").lowercase()
    }

    // Construye la lista de productos con líneas de pedido
    private fun buildProductWithOrderList(
        orderList: List<OrderLineProduct>,
        filterQuery: String? = null
    ) {
        val productList = mutableListOf<Product>()
        for (common in initialCommonProducts) {
            val matchingOrder = orderList.find { it.productId == common.id }
            if (matchingOrder != null) {
                productList.add(ProductWithOrderLine(common, matchingOrder))
            } else {
                productList.add(common)
            }
        }
        Timber.i("SYNC_INIT_COMMON_PRODUCTS - Productos inicializados (${initialCommonProducts.size}): $initialCommonProducts")
        val filteredList = if (!filterQuery.isNullOrBlank()) {
            val q = filterQuery.foldSearch()
            productList.filter { p ->
                val company = p.companyName.foldSearch()
                val text = p.toString().foldSearch()
                company.contains(q) || text.contains(q)
            }
        } else productList
        val productsWithOrderLine = filteredList.filterIsInstance<ProductWithOrderLine>()
        val groupedByCompany = filteredList.groupBy { it.companyName }.toSortedMap()
        _state.update {
            it.copy(
                productsGroupedByCompany = groupedByCompany,
                productsOrderLineList = productsWithOrderLine,
                hasOrderLine = orderList.isNotEmpty()
            )
        }
    }

    private suspend fun applySearchFilter() {
        if (!::initialCommonProducts.isInitialized) return
        val currentOrderLines = orderModel.getOrderLinesList()
        // reconstruye la lista fusionada + aplica el filtro activo (puede ser cadena vacía)
        buildProductWithOrderList(
            orderList = currentOrderLines,
            filterQuery = state.value.searchQuery
        )
    }

    // Maneja los eventos de la UI relacionados con la creación de pedidos
    fun onEvent(newEvent: NewOrderEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (newEvent) {
                is NewOrderEvent.GoOut -> {
                    _state.update { it.copy(goOut = true, showSearch = false, searchQuery = "") }
                }

                is NewOrderEvent.StartOrder -> {
                    val selectedProduct = state.value.productsGroupedByCompany.values.flatten()
                        .find { it.id == newEvent.productId }
                    selectedProduct?.let {
                        orderModel.addLocalOrderLine(
                            newEvent.productId,
                            it.companyName
                        )
                        // Siempre reconstruir la lista global y grouping tras añadir línea
                        buildProductWithOrderList(orderModel.getOrderLinesList())
                    }
                }

                is NewOrderEvent.PlusQuantityProduct -> {
                    val productUpdated =
                        state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.plus(1)
                        orderModel.updateProductStock(newEvent.productId, newQuantity)
                        // Siempre reconstruir la lista global y grouping tras actualizar cantidad
                        buildProductWithOrderList(orderModel.getOrderLinesList())
                    }
                }

                is NewOrderEvent.MinusQuantityProduct -> {
                    val productUpdated =
                        state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.minus(1)
                        if (newQuantity == 0) {
                            orderModel.deleteOrderLineLocal(newEvent.productId)
                            // Siempre reconstruir la lista global y grouping tras borrar línea
                            buildProductWithOrderList(orderModel.getOrderLinesList())
                        } else {
                            orderModel.updateProductStock(newEvent.productId, newQuantity)
                            // Siempre reconstruir la lista global y grouping tras actualizar cantidad
                            buildProductWithOrderList(orderModel.getOrderLinesList())
                        }
                    }
                }

                NewOrderEvent.HideShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = false) }
                }

                NewOrderEvent.ShowShoppingCart -> {
                    Timber.i("SYNC_VIEWMODEL_EVENT - Ejecutando ShowShoppingCart, productsOrderLineList.size = ${state.value.productsOrderLineList.size}")
                    _state.update { it.copy(showShoppingCart = true, showSearch = false) }
                }

                is NewOrderEvent.ShowSearch -> {
                    _state.update { it.copy(showSearch = true) }
                }

                is NewOrderEvent.HideSearch -> {
                    _state.update { it.copy(showSearch = false, searchQuery = "") }
                    searchJob?.cancel()
                    searchJob = viewModelScope.launch(Dispatchers.IO) {
                        applySearchFilter()
                    }
                }

                is NewOrderEvent.UpdateSearchQuery -> {
                    _state.update { it.copy(searchQuery = newEvent.query) }
                    searchJob?.cancel()
                    searchJob = viewModelScope.launch(Dispatchers.IO) {
                        delay(300)
                        applySearchFilter()
                    }
                }

                NewOrderEvent.PushOrder -> {
                    _state.update { it.copy(isOrdering = true) }
                    val updatedProductsOrderLineList = state.value.productsOrderLineList.map {
                        val adjustedQuantity = when (it.container) {
                            ContainerType.COMMIT_MANGOES.value -> state.value.kgMangoes
                            ContainerType.COMMIT_AVOCADOS.value -> state.value.kgAvocados
                            else -> it.orderLine.quantity
                        }
                        /*
                        val adjustedSubtotal =
                            if (it.container == ContainerType.COMMIT_MANGOES.value ||
                                it.container == ContainerType.COMMIT_AVOCADOS.value
                            ) {
                                it.price.toDouble()
                            } else {
                                it.getAmount()
                            }

                         */
                        val updatedOrderLine =
                            it.orderLine.copy(quantity = adjustedQuantity)//, subtotal = adjustedSubtotal)

                        it.copy(orderLine = updatedOrderLine)
                    }

                    val productsInOrder = updatedProductsOrderLineList.map {
                        OrderLineProduct(
                            orderId = it.getOrderId(),
                            userId = it.getUserId(),
                            productId = it.id,
                            companyName = it.companyName,
                            quantity = it.quantity,
                            subtotal = it.getAmount(),
                            week = it.getWeek()
                        )
                    }

                    val checkResult = checkCommitmentsUseCase(productsInOrder)

                    if (checkResult.isSuccess) {
                        orderModel.pushOrderLinesToFirebase(updatedProductsOrderLineList).fold(
                            onSuccess = {
                                updateTableTimestampsUseCase("orders")
                                _state.update { it.copy(showPopup = PopupType.ORDER_ADDED) }
                                _state.update { it.copy(isOrdering = false) }
                            },
                            onFailure = { throwable ->
                                throwable.printStackTrace()
                                _state.update { it.copy(isOrdering = false) }
                            }
                        )
                    } else {
                        val errorMessage = checkResult.exceptionOrNull()?.message ?: "Error desconocido"
                        _state.update {
                            it.copy(
                                showPopup = PopupType.MISSING_COMMIT,
                                errorMessage = errorMessage
                            )
                        }
                        _state.update { it.copy(isOrdering = false) }
                    }
                }

                NewOrderEvent.DeleteOrder -> {
                    _state.update { it.copy(isDeletingOrder = true) }
                    for (orderLines in state.value.ordersFromExistingOrder.values.flatten()) {
                        val product = initialCommonProducts.find { it.id == orderLines.product.id }
                        product?.let {
                            updateProductStockUseCase(
                                it.id,
                                it.stock.plus(orderLines.quantity)
                            )
                        }
                    }
                    orderModel.deleteOrder()
                    updateTableTimestampsUseCase("orders")
                    _state.update { it.copy(isDeletingOrder = false) }
                    _state.update { it.copy(goOut = true) }
                }

                NewOrderEvent.HideDialog -> _state.update { it.copy(showPopup = PopupType.NONE) }
                NewOrderEvent.ShowAreYouSureDeleteOrder -> _state.update { it.copy(showPopup = PopupType.ARE_YOU_SURE_DELETE) }
            }
        }
    }

    // Recarga los datos principales del pedido y productos
    private fun forceReload() {
        Timber.i("SYNC_SYNC_FORCE_RELOAD - Ejecutando forceReload()")
        viewModelScope.launch(Dispatchers.IO) {
            val flow = state.value.flow
            if (flow == null) {
                Timber.w("SYNC_FLOW - flow es null en reload, calculando una vez")
                val today = DayOfWeek.of(getCurrentWeek())
                val deliveryDay = getDeliveryDayUseCase()
                val isNewOrder = isNewOrderBranch(today, deliveryDay)
                _state.update { it.copy(flow = if (isNewOrder) OrderFlow.CURRENT_WEEK else OrderFlow.LAST_WEEK) }
            }
            val result = withTimeoutOrNull(3_000) {
                try {
                    val currentUserResult = checkCurrentUserLoggedUseCase()
                    val currentUser = currentUserResult.getOrNull() ?: return@withTimeoutOrNull
                    _state.update {
                        it.copy(
                            currentDay = DayOfWeek.of(getCurrentWeek()),
                            kgMangoes = currentUser.tropical1.roundToInt(),
                            kgAvocados = currentUser.tropical2.roundToInt()
                        )
                    }
                    try {
                        val availableProducts =
                            getAvailableProductsUseCase(forceFromServer = true).first()
                        if (availableProducts.isNotEmpty()) {
                            initialCommonProducts = availableProducts
                            Timber.i("SYNC_INIT_COMMON_PRODUCTS - Productos inicializados (${initialCommonProducts.size}): $initialCommonProducts")
                            val groupedByCompany =
                                availableProducts.groupBy { it.companyName }.toSortedMap()
                            _state.update {
                                it.copy(
                                    productsGroupedByCompany = groupedByCompany
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "SYNC_SYNC_Error al forzar carga de productos en forceReload")
                    }
                    val job1 = async {
                        withTimeoutOrNull(2_000) {
                            val measureList = getAllMeasuresUseCase()
                            _state.update { it.copy(measures = measureList) }
                        }
                    }
                    val job2 = async {
                        withTimeoutOrNull(2_000) {
                            val containerList = getAllContainersUseCase()
                            _state.update { it.copy(containers = containerList) }
                        }
                    }
                    val job3 = async {
                        withTimeoutOrNull(2_000) {
                            when (state.value.flow) {
                                OrderFlow.CURRENT_WEEK -> {
                                    val existOrder = orderModel.checkIfExistOrderInFirebase().getOrDefault(false)
                                    if (existOrder) {
                                        handleCurrentWeekOrders()
                                    } else {
                                        if (loadAvailableProducts(getAvailableProductsUseCase)) {
                                            _state.update { it.copy(uiState = NewOrderUiMode.SELECT_PRODUCTS) }
                                        }
                                    }
                                }
                                OrderFlow.LAST_WEEK -> {
                                    val existOrder = orderModel.checkIfExistLastWeekOrderInFirebase().getOrDefault(false)
                                    if (existOrder) {
                                        observeOrderLines(isEdit = false)
                                    } else {
                                        _state.update { it.copy(uiState = NewOrderUiMode.ERROR, errorMessage = "No hay pedido anterior disponible") }
                                    }
                                }
                                null -> { /* ya cubierto arriba */ }
                            }
                        }
                    }
                    awaitAll(job1, job2, job3)
                } finally {
                    // No hay loader, pero aquí podría ponerse si se requiere
                }
            }
            if (result == null) {
                Timber.e("SYNC_SYNC_TIMEOUT: El ciclo principal del ViewModel ha excedido timeout. [forceReload]")
            }
        }
    }

    private fun observeOrderLines(isEdit: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            orderModel.getOrderLinesFromCurrentWeek().collectLatest { ordersReceived ->
                Timber.i("SYNC_ORDERLINES_FLOW - Recibidas ${ordersReceived.size} orderlines del Flow")
                // Eliminado log detallado de cada orderline para salida limpia
                if (!::initialCommonProducts.isInitialized) {
                    Timber.w("SYNC_initialCommonProducts NO inicializado, descargando productos antes de mapear orderlines")
                    val availableProducts =
                        getAvailableProductsUseCase().firstOrNull() ?: emptyList()
                    initialCommonProducts = availableProducts
                    // Eliminado log detallado de productos inicializados para salida limpia
                }
                val mappedOrderLines =
                    mapOrderLinesWithProductsUseCase(ordersReceived, initialCommonProducts)
                Timber.i("SYNC_ORDERLINES_FLOW - Orderlines mapeadas (${mappedOrderLines.size})")
                val groupedByCompany = mappedOrderLines.groupBy { it.companyName }.toSortedMap()

                val newUiState =
                    if (isEdit) NewOrderUiMode.EDIT_ORDER else NewOrderUiMode.SHOW_PREVIOUS_ORDER

                Timber.i("SYNC_ORDERLINES_FLOW - Estado actualizado: orderLinesByCompanyName=${groupedByCompany.size}, ordersFromExistingOrder=${mappedOrderLines.groupBy { it.product }.size}")
                _state.update { orderState ->
                    orderState.copy(
                        orderLinesByCompanyName = groupedByCompany,
                        ordersFromExistingOrder = mappedOrderLines.groupBy { it.product },
                        uiState = newUiState
                    )
                }
            }
        }
    }

    fun forceReloadOnce() {
        if (hasForcedReload) {
            Timber.i("SYNC_DEBUG - forceReloadOnce() ya fue ejecutado, se omite.")
            return
        }
        Timber.i("SYNC_DEBUG - Ejecutando forceReloadOnce() por primera vez")
        hasForcedReload = true
        forceReload()
    }
}
    // Helper to determine if we are in the new order branch window
    private fun isNewOrderBranch(today: DayOfWeek, deliveryDay: WeekDay): Boolean {
        val deliveryDow = deliveryDay.toJavaDayOfWeek()
        val startOfNewOrder = when (deliveryDow) {
            DayOfWeek.SUNDAY -> {
                deliveryDow
            }
            else -> {
                deliveryDow.plus(1) // desde delivery+2 hasta domingo
            }   // Aunque el día siguiente al dia de reparto no se hacen pedidos,
                // aqui no se entra, se controla en home
        }
        return today >= startOfNewOrder
    }