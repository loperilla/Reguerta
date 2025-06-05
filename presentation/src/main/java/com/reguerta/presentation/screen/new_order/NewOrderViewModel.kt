package com.reguerta.presentation.screen.new_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.enums.ContainerType
import com.reguerta.domain.model.CommonProduct
import com.reguerta.domain.model.OrderLineProduct
import com.reguerta.domain.model.ProductWithOrderLine
import com.reguerta.domain.model.NewOrderModel
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.containers.GetAllContainersUseCase
import com.reguerta.domain.usecase.measures.GetAllMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.MapOrderLinesWithProductsUseCase
import com.reguerta.domain.usecase.products.CheckCommitmentsUseCase
import com.reguerta.domain.usecase.products.GetAvailableProductsUseCase
import com.reguerta.domain.usecase.products.UpdateProductStockUseCase
import com.reguerta.domain.usecase.config.UpdateTableTimestampsUseCase
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
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
) : ViewModel() {
    private var _state: MutableStateFlow<NewOrderState> = MutableStateFlow(NewOrderState())
    val state: StateFlow<NewOrderState> = _state.asStateFlow()

    private lateinit var initialCommonProducts: List<CommonProduct>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Determinar si estamos en la rama de fin de semana (jueves a domingo) o entre semana (lunes a miércoles)
            val today = java.time.LocalDate.now()
            val isWeekendBranch = today.dayOfWeek in listOf(
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            )
            Timber.i("FLOW_BRANCH - Día actual: ${today.dayOfWeek}, isWeekendBranch: $isWeekendBranch")
            /***********************************************************************
             * ANÁLISIS DE FLUJOS EN withTimeoutOrNull(15_000) DEL INIT
             *
             * El bloque principal de inicialización está envuelto en un withTimeoutOrNull(15_000).
             * Dentro de este bloque se ejecutan varias operaciones secuenciales y en paralelo (async).
             *
             * 1) OPERACIONES PREVIAS AL PARALELISMO:
             *    - checkCurrentUserLoggedUseCase()
             *      > Llama a un use case que probablemente consulta preferencia local o caché de usuario.
             *      > Operación SEGURA. No depende de red ni de un flow prolongado.
             *    - getCurrentWeek()
             *      > Use case sencillo, probablemente cálculo local.
             *      > Operación SEGURA.
             *    - loadAvailableProducts(getAvailableProductsUseCase)
             *      > Esta función ESPECIALMENTE IMPORTANTE:
             *      > Internamente usa getAvailableProductsUseCase().first(), que es un Flow (puede ser de Firestore).
             *      > Si el flow no emite nunca, puede quedarse esperando.
             *      > Tiene un bucle de reintentos (hasta 10, con delay de 300ms) y fallback a getAllProductsDirect().
             *      > getAllProductsDirect() es una llamada directa (probable acceso a red/Firestore).
             *      > Si Firestore se cuelga o la red va lenta, aquí puede haber bloqueo.
             *      > OPERACIÓN POTENCIALMENTE PROBLEMÁTICA: Si el flow nunca emite o la red/firestore está lenta, puede agotar el timeout.
             *
             * 2) OPERACIONES EN PARALELO (listOf(async { ... }).awaitAll()):
             *    - async #1: getAllMeasuresUseCase()
             *      > Llama a un use case para obtener medidas.
             *      > Probable acceso a Firestore o BD remota.
             *      > Si Firestore está lento o el flow nunca emite, puede colgarse.
             *      > OPERACIÓN POTENCIALMENTE PROBLEMÁTICA.
             *    - async #2: getAllContainersUseCase()
             *      > Similar al anterior, pero para contenedores.
             *      > Probable acceso a Firestore o BD remota.
             *      > OPERACIÓN POTENCIALMENTE PROBLEMÁTICA.
             *    - async #3: handleLastWeekOrders() / handleCurrentWeekOrders()
             *      > Dependiendo del día, llama a uno u otro.
             *      > Ambos llaman a orderModel.checkIfExistLastWeekOrderInFirebase() o checkIfExistOrderInFirebase()
             *      > Estos métodos acceden a Firestore (llamada de red).
             *      > Si existe pedido, llama a loadOrderLinesFromCurrentWeek(), que hace collectLatest sobre un Flow (Firestore).
             *      > Si no existe pedido, puede llamar a loadNewOrderLines(), que también hace collectLatest sobre un Flow.
             *      > Si el flow nunca emite, o Firestore está lento, puede quedarse esperando.
             *      > OPERACIÓN POTENCIALMENTE PROBLEMÁTICA: Puede colgarse si Firestore/flow no responde.
             *
             * SÍNTESIS:
             * - SEGURAS:
             *   - checkCurrentUserLoggedUseCase()
             *   - getCurrentWeek()
             * - POTENCIALMENTE PROBLEMÁTICAS (pueden agotar el timeout si Firestore/Flow/red no responde):
             *   - loadAvailableProducts(getAvailableProductsUseCase) [por uso de Flow y getAllProductsDirect()]
             *   - async { getAllMeasuresUseCase() }
             *   - async { getAllContainersUseCase() }
             *   - async { handleLastWeekOrders() / handleCurrentWeekOrders() } (por uso de métodos que acceden a Firestore y collectLatest sobre Flows)
             *
             * NOTA: El problema principal surge si alguna llamada a Firestore/Flow no responde o se queda esperando indefinidamente.
             *       Como awaitAll espera a que TODOS los async terminen, si uno se cuelga, los demás también "esperan" y el timeout global se agota.
             *
             * EVIDENCIA EN LOGS:
             * - Los errores de timeout aparecen asociados a los async de pedidos y contenedores, lo que confirma que son candidatos a colgarse.
             ***********************************************************************/
            Timber.i("SYNC_TRACE - Inicio de bloque withTimeoutOrNull de 15s")
            val result = withTimeoutOrNull(3_000) {
                try {
                    Timber.i("SYNC_INIT de NewOrderViewModel lanzado a las ${System.currentTimeMillis()}")
                    val currentUserResult = checkCurrentUserLoggedUseCase()
                    val currentUser = currentUserResult.getOrNull()

                    if (currentUser == null) {
                        Timber.w("SYNC_INIT - currentUser es null, abortando carga inicial en NewOrderViewModel")
                        _state.update {
                            Timber.i("SYNC_DEBUG isLoading puesto a false en [init: currentUser==null]")
                            it.copy(isLoading = false)
                        }
                        return@withTimeoutOrNull
                    }

                    _state.update {
                        it.copy(
                            isLoading = true,
                            currentDay = DayOfWeek.of(getCurrentWeek()),
                            kgMangoes = currentUser.tropical1.roundToInt(),
                            kgAvocados = currentUser.tropical2.roundToInt()
                        )
                    }
                    Timber.i("SYNC_DEBUG Antes de loadAvailableProducts en [init]")
                    if (!loadAvailableProducts(getAvailableProductsUseCase)) {
                        Timber.i("SYNC_DEBUG isLoading puesto a false en [init: loadAvailableProducts==false]")
                        _state.update { it.copy(isLoading = false) }
                        return@withTimeoutOrNull
                    }
                    Timber.i("SYNC_DEBUG Después de loadAvailableProducts en [init]")

                    // --- NUEVO BLOQUE: jobs individuales para detectar bloqueo ---
                    val job1 = async {
                        withTimeoutOrNull(2_000) {
                            val measureList = getAllMeasuresUseCase()
                            _state.update { it.copy(measures = measureList) }
                        } ?: Timber.w("SYNC_TIMEOUT interno en medidas")
                    }
                    val job2 = async {
                        withTimeoutOrNull(2_000) {
                            val containerList = getAllContainersUseCase()
                            _state.update { it.copy(containers = containerList) }
                        } ?: Timber.w("SYNC_TIMEOUT interno en containers")
                    }
                    val job3 = async {
                        withTimeoutOrNull(2_000) {
                            // Usar isWeekendBranch para decidir el flujo
                            if (isWeekendBranch) {
                                handleCurrentWeekOrders()
                            } else {
                                handleLastWeekOrders()
                            }
                        } ?: Timber.w("SYNC_TIMEOUT interno en orders")
                    }

                    val result = withTimeoutOrNull(15_000) {
                        awaitAll(job1, job2, job3)
                        Timber.i("SYNC_TRACE - awaitAll completado sin timeout")
                    }

                    Timber.i("SYNC_JOB_STATUS - job1 isCompleted=${job1.isCompleted}, job2 isCompleted=${job2.isCompleted}, job3 isCompleted=${job3.isCompleted}")
                } finally {
                    Timber.i("SYNC_TRACE - Entrando en finally del bloque withTimeoutOrNull")
                    Timber.i("🔥 SYNC_UI: Ocultando loader desde init")
                    _state.update { it.copy(isLoading = false) }
                    Timber.i("SYNC_DEBUG isLoading puesto a false en finally [init]")
                }
            }
            if (result == null) {
                Timber.i("SYNC_TRACE - withTimeoutOrNull devolvió null por timeout")
                Timber.e("SYNC_TIMEOUT: El ciclo principal del ViewModel ha excedido 15s. Forzando loader a false.")
                _state.update {
                    Timber.i("SYNC_DEBUG isLoading puesto a false tras timeout [init]")
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun loadAvailableProducts(getAvailableProductsUseCase: GetAvailableProductsUseCase): Boolean {
        Timber.i("SYNC_Entrando en loadAvailableProducts()")
        var intentos = 0
        val maxIntentos = 10
        val delayMillis = 300L
        while (intentos < maxIntentos) {
            try {
                Timber.i("SYNC_DEBUG Antes de getAvailableProductsUseCase().first() en loadAvailableProducts")
                val list = getAvailableProductsUseCase().first()
                Timber.i("SYNC_DEBUG Después de getAvailableProductsUseCase().first() en loadAvailableProducts")
                Timber.i("SYNC_loadAvailableProducts - intento ${intentos + 1}: productos obtenidos: ${list.size} - ids: ${list.joinToString { it.id }}")
                if (list.isNotEmpty()) {
                    // Solo actualizamos el estado si la lista cambia
                    if (::initialCommonProducts.isInitialized && initialCommonProducts == list) {
                        Timber.i("SYNC_loadAvailableProducts - la lista recibida es igual a la actual, no actualizo el estado")
                        return true
                    }
                    initialCommonProducts = list
                    val groupedByCompany = list.groupBy { it.companyName }.toSortedMap()
                    _state.update {
                        it.copy(
                            productsGroupedByCompany = groupedByCompany
                        )
                    }
                    Timber.i("SYNC_loadAvailableProducts - estado actualizado con productos: ${groupedByCompany.values.flatten().size}")
                    return true
                } else if (intentos == 1) {
                    // Tras 2 intentos (intentos 0 y 1), forzamos carga directa
                    Timber.i("SYNC_DEBUG Antes de getAllProductsDirect en loadAvailableProducts")
                    val directList = getAvailableProductsUseCase.getAllProductsDirect().getOrDefault(emptyList())
                    Timber.i("SYNC_DEBUG Después de getAllProductsDirect en loadAvailableProducts")
                    Timber.i("SYNC_loadAvailableProducts - getAllProductsDirect(): ${directList.size} productos")
                    if (directList.isNotEmpty()) {
                        if (::initialCommonProducts.isInitialized && initialCommonProducts == directList) {
                            Timber.i("SYNC_loadAvailableProducts - la lista directa recibida es igual a la actual, no actualizo el estado")
                            return true
                        }
                        initialCommonProducts = directList
                        val groupedByCompany = directList.groupBy { it.companyName }.toSortedMap()
                        _state.update {
                            it.copy(
                                productsGroupedByCompany = groupedByCompany
                            )
                        }
                        Timber.i("SYNC_loadAvailableProducts - estado actualizado con productos directos: ${groupedByCompany.values.flatten().size}")
                        return true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e(e, "SYNC_Error en loadAvailableProducts: ${e.message}")
                handleError(e)
                return false
            }
            // Si está vacío, esperamos y reintentamos
            intentos++
            Timber.i("SYNC_loadAvailableProducts - lista vacía, reintentando (${intentos + 1})...")
            delay(delayMillis)
        }
        Timber.e("SYNC_loadAvailableProducts - No se pudieron obtener productos tras $maxIntentos intentos")
        handleError(IllegalStateException("No se pudieron obtener productos tras $maxIntentos intentos"))
        return false
    }

    private suspend fun handleLastWeekOrders() {
        Timber.i("SYNC_handleLastWeekOrders: comprobando si existe pedido anterior...")
        Timber.i("SYNC_DEBUG Antes de checkIfExistLastWeekOrderInFirebase en handleLastWeekOrders")
        orderModel.checkIfExistLastWeekOrderInFirebase().fold(
            onSuccess = { existOrder ->
                Timber.i("SYNC_DEBUG Después de checkIfExistLastWeekOrderInFirebase en handleLastWeekOrders")
                Timber.i("SYNC_handleLastWeekOrders - existe pedido anterior: $existOrder")
                _state.update { it.copy(isExistOrder = existOrder) }
                if (existOrder) {
                    Timber.i("SYNC_DEBUG Antes de loadOrderLinesFromCurrentWeek en handleLastWeekOrders")
                    loadOrderLinesFromCurrentWeek()
                    Timber.i("SYNC_DEBUG Después de loadOrderLinesFromCurrentWeek en handleLastWeekOrders")
                } else {
                    _state.update {
                        it.copy(
                            hasOrderLine = false,
                            isExistOrder = false
                        )
                    }
                }
            },
            onFailure = {
                Timber.e(it, "SYNC_handleLastWeekOrders - onFailure: ${it.message}")
                handleError(it)
            }
        )
    }

    private suspend fun handleCurrentWeekOrders() {
        Timber.i("SYNC_handleCurrentWeekOrders: comprobando si existe pedido actual...")
        Timber.i("SYNC_DEBUG Antes de checkIfExistOrderInFirebase en handleCurrentWeekOrders")
        orderModel.checkIfExistOrderInFirebase().fold(
            onSuccess = { existOrder ->
                Timber.i("SYNC_DEBUG Después de checkIfExistOrderInFirebase en handleCurrentWeekOrders")
                Timber.i("SYNC_handleCurrentWeekOrders - existe pedido actual: $existOrder")
                _state.update { it.copy(isExistOrder = existOrder) }
                if (existOrder) {
                    loadOrderLinesFromCurrentWeek()
                } else {
                    Timber.i("SYNC_DEBUG Antes de loadNewOrderLines en handleCurrentWeekOrders")
                    loadNewOrderLines()
                    Timber.i("SYNC_DEBUG Después de loadNewOrderLines en handleCurrentWeekOrders")
                }
            },
            onFailure = {
                Timber.e(it, "SYNC_handleCurrentWeekOrders - onFailure: ${it.message}")
                handleError(it)
            }
        )
    }

    private suspend fun loadOrderLinesFromCurrentWeek() {
        Timber.i("SYNC_Entrando en loadOrderLinesFromCurrentWeek()")
        Timber.i("SYNC_DEBUG Antes de getOrderLinesFromCurrentWeek.firstOrNull en loadOrderLinesFromCurrentWeek")
        val ordersReceived = withTimeoutOrNull(1000) {
            orderModel.getOrderLinesFromCurrentWeek().firstOrNull()
        } ?: return
        Timber.i("DEBUG_ORDERLINES - orderLines recibidas: ${ordersReceived.size}")
        ordersReceived.firstOrNull()?.let { first ->
            Timber.i("DEBUG_ORDERLINES - Primera orderLine recibida: $first")
        }
        Timber.i("SYNC_DEBUG Dentro de firstOrNull en loadOrderLinesFromCurrentWeek")
        val mappedOrderLines =
            mapOrderLinesWithProductsUseCase(ordersReceived, initialCommonProducts)
        val groupedByCompany = mappedOrderLines.groupBy { it.companyName }.toSortedMap()
        Timber.i("SYNC_loadOrderLinesFromCurrentWeek - orderLines agrupadas: ${groupedByCompany.values.flatten().size}")
        _state.update {
            it.copy(
                hasOrderLine = true,
                orderLinesByCompanyName = groupedByCompany,
                ordersFromExistingOrder = mappedOrderLines.groupBy { it.product }
            )
        }
        Timber.i("SYNC_DEBUG Después de getOrderLinesFromCurrentWeek.firstOrNull en loadOrderLinesFromCurrentWeek")
    }

    private suspend fun loadNewOrderLines() {
        Timber.i("SYNC_Entrando en loadNewOrderLines()")
        Timber.i("SYNC_DEBUG Antes de getOrderLines.collectLatest en loadNewOrderLines")
        withTimeoutOrNull(1000) {
            orderModel.getOrderLines().collectLatest { orderList ->
                Timber.i("SYNC_DEBUG Dentro de collectLatest en loadNewOrderLines")
                Timber.i("SYNC_loadNewOrderLines - orderList.size=${orderList.size}")
                if (orderList.isNotEmpty()) {
                    Timber.i("SYNC_DEBUG Antes de buildProductWithOrderList en loadNewOrderLines")
                    buildProductWithOrderList(orderList)
                    Timber.i("SYNC_DEBUG Después de buildProductWithOrderList en loadNewOrderLines")
                } else {
                    val availableProducts = getAvailableProductsUseCase().first()
                    Timber.i("SYNC_loadNewOrderLines - productos disponibles: ${availableProducts.size}")
                    _state.update {
                        it.copy(
                            productsGroupedByCompany = availableProducts.groupBy { it.companyName }.toSortedMap(),
                            hasOrderLine = false
                        )
                    }
                }
            }
        } ?: Timber.w("SYNC_TIMEOUT interno en collectLatest de loadNewOrderLines")
        Timber.i("SYNC_DEBUG Después de getOrderLines.collectLatest en loadNewOrderLines")
    }

    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }

    private fun buildProductWithOrderList(orderList: List<OrderLineProduct>) {
        Timber.i("SYNC_Entrando en buildProductWithOrderList(), orderList.size=${orderList.size}")
        val productList = initialCommonProducts.map { common ->
            orderList.find { it.productId == common.id }?.let { order ->
                ProductWithOrderLine(common, order)
            } ?: common
        }
        val productsWithOrderLine = productList.filterIsInstance<ProductWithOrderLine>()
        val groupedByCompany = productList.groupBy { it.companyName }.toSortedMap()
        Timber.i("SYNC_buildProductWithOrderList - productsOrderLineList.size=${productsWithOrderLine.size}")
        _state.update {
            it.copy(
                hasOrderLine = productsWithOrderLine.isNotEmpty(),
                productsGroupedByCompany = groupedByCompany,
                productsOrderLineList = productsWithOrderLine
            )
        }
    }

    fun onEvent(newEvent: NewOrderEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (newEvent) {
                is NewOrderEvent.GoOut -> {
                    _state.update { it.copy(goOut = true) }
                }

                is NewOrderEvent.StartOrder -> {
                    val selectedProduct = state.value.productsGroupedByCompany.values.flatten()
                        .find { it.id == newEvent.productId }
                    selectedProduct?.let {
                        orderModel.addLocalOrderLine(
                            newEvent.productId,
                            it.companyName
                        )
                    }
                }

                is NewOrderEvent.PlusQuantityProduct -> {
                    val productUpdated =
                        state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.plus(1)
                        orderModel.updateProductStock(newEvent.productId, newQuantity)
                        _state.update { currentState ->
                            val newList = currentState.productsOrderLineList.map {
                                if (it.id == newEvent.productId) it.copy(
                                    orderLine = it.orderLine.copy(
                                        quantity = newQuantity
                                    )
                                ) else it
                            }
                            currentState.copy(productsOrderLineList = newList)
                        }
                    }
                }

                is NewOrderEvent.MinusQuantityProduct -> {
                    val productUpdated =
                        state.value.productsOrderLineList.singleOrNull { it.id == newEvent.productId }
                    productUpdated?.let { line ->
                        val newQuantity = line.quantity.minus(1)
                        if (newQuantity == 0) {
                            orderModel.deleteOrderLineLocal(newEvent.productId)
                            _state.update { currentState ->
                                currentState.copy(productsOrderLineList = currentState.productsOrderLineList.filter { it.id != newEvent.productId })
                            }
                        } else {
                            orderModel.updateProductStock(newEvent.productId, newQuantity)
                            _state.update { currentState ->
                                val newList = currentState.productsOrderLineList.map {
                                    if (it.id == newEvent.productId) it.copy(
                                        orderLine = it.orderLine.copy(
                                            quantity = newQuantity
                                        )
                                    ) else it
                                }
                                currentState.copy(productsOrderLineList = newList)
                            }
                        }
                    }
                }

                NewOrderEvent.HideShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = false) }
                }

                NewOrderEvent.ShowShoppingCart -> {
                    _state.update { it.copy(showShoppingCart = true) }
                }

                NewOrderEvent.PushOrder -> {
                    val updatedProductsOrderLineList = state.value.productsOrderLineList.map {
                        val adjustedQuantity = when (it.container) {
                            ContainerType.COMMIT_MANGOES.value -> state.value.kgMangoes
                            ContainerType.COMMIT_AVOCADOS.value -> state.value.kgAvocados
                            else -> it.orderLine.quantity
                        }
                        val adjustedSubtotal =
                            if (it.container == ContainerType.COMMIT_MANGOES.value ||
                                it.container == ContainerType.COMMIT_AVOCADOS.value
                            ) {
                                it.price.toDouble()
                            } else {
                                it.getAmount()
                            }
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
                            },
                            onFailure = { throwable ->
                                throwable.printStackTrace()
                            }
                        )
                    } else {
                        val errorMessage =
                            checkResult.exceptionOrNull()?.message ?: "Error desconocido"
                        _state.update {
                            it.copy(
                                showPopup = PopupType.MISSING_COMMIT,
                                errorMessage = errorMessage
                            )
                        }
                    }
                }

                NewOrderEvent.DeleteOrder -> {
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
                    _state.update { it.copy(goOut = true) }
                }

                NewOrderEvent.HideDialog -> _state.update { it.copy(showPopup = PopupType.NONE) }
                NewOrderEvent.ShowAreYouSureDeleteOrder -> _state.update { it.copy(showPopup = PopupType.ARE_YOU_SURE_DELETE) }
            }
        }
    }

    fun forceReload() {
        Timber.i("SYNC_forceReload lanzada en ${this::class.simpleName} a las ${System.currentTimeMillis()}")
        viewModelScope.launch(Dispatchers.IO) {
            // Determinar si estamos en la rama de fin de semana (jueves a domingo) o entre semana (lunes a miércoles)
            val today = java.time.LocalDate.now()
            val isWeekendBranch = today.dayOfWeek in listOf(
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
            )
            Timber.i("FLOW_BRANCH - Día actual: ${today.dayOfWeek}, isWeekendBranch: $isWeekendBranch")
            /***********************************************************************
             * ANÁLISIS DE FLUJOS EN withTimeoutOrNull(3_000) DE forceReload()
             *
             * El análisis es análogo al del init:
             * - checkCurrentUserLoggedUseCase() y getCurrentWeek() son SEGURAS (locales).
             * - getAllProductsDirect() es POTENCIALMENTE PROBLEMÁTICA (puede depender de red/Firestore/Flow).
             * - async { getAllMeasuresUseCase() }, async { getAllContainersUseCase() }, async { handleLastWeekOrders()/handleCurrentWeekOrders() }
             *   son POTENCIALMENTE PROBLEMÁTICAS (acceso a Firestore/red/Flow).
             ***********************************************************************/
            val result = withTimeoutOrNull(3_000) {
                try {
                    val currentUserResult = checkCurrentUserLoggedUseCase()
                    Timber.i("SYNC_TRACE - currentUserResult: $currentUserResult")
                    val currentUser = currentUserResult.getOrNull()

                    if (currentUser == null) {
                        _state.update {
                            Timber.i("SYNC_DEBUG isLoading puesto a false en [forceReload: currentUser==null]")
                            it.copy(isLoading = false)
                        }
                        return@withTimeoutOrNull
                    }

                    val currentWeekId = getCurrentWeek()
                    Timber.i("DEBUG_FORCE_RELOAD - Semana actual: $currentWeekId")

                    _state.update {
                        it.copy(
                            isLoading = true,
                            currentDay = DayOfWeek.of(getCurrentWeek()),
                            kgMangoes = currentUser.tropical1.roundToInt(),
                            kgAvocados = currentUser.tropical2.roundToInt()
                        )
                    }

                    // getAllProductsDirect() puede colgarse si la red/Firestore está lenta.
                    Timber.i("SYNC_DEBUG Antes de getAllProductsDirect en forceReload")
                    val allProductsResult = getAvailableProductsUseCase.getAllProductsDirect()
                    Timber.i("SYNC_TRACE - Resultado getAllProductsDirect: $allProductsResult")
                    Timber.i("SYNC_DEBUG Después de getAllProductsDirect en forceReload")
                    allProductsResult.onSuccess { products ->
                        Timber.i("SYNC_getAllProducts directa en forceReload - productos recibidos: ${products.size}")
                        Timber.i("SYNC_getAllProducts directa en forceReload - ids: ${products.joinToString { it.id }}")
                        if (products.isNotEmpty()) {
                            initialCommonProducts = products
                            Timber.i("SYNC_forceReload - initialCommonProducts actualizado: ${initialCommonProducts.size}")
                            val groupedByCompany = products.groupBy { it.companyName }.toSortedMap()
                            _state.update {
                                it.copy(
                                    productsGroupedByCompany = groupedByCompany
                                )
                            }
                        }
                    }.onFailure { error ->
                        Timber.e(error, "SYNC_Error en getAllProducts directa en forceReload")
                    }

                    // --- ANÁLISIS DE async { ... } EN ESTE BLOQUE ---
                    // async #1: getAllMeasuresUseCase() --> POTENCIALMENTE PROBLEMÁTICO (Firestore/red)
                    // async #2: getAllContainersUseCase() --> POTENCIALMENTE PROBLEMÁTICO (Firestore/red)
                    // async #3: handleLastWeekOrders() / handleCurrentWeekOrders() --> POTENCIALMENTE PROBLEMÁTICO (Firestore/red/Flow)
                    val job1 = async {
                        withTimeoutOrNull(2_000) {
                            val measureList = getAllMeasuresUseCase()
                            Timber.i("SYNC_collect de MEASURES en ${this@NewOrderViewModel::class.simpleName} a las ${System.currentTimeMillis()} - tamaño: ${measureList.size}")
                            _state.update { it.copy(measures = measureList) }
                        } ?: Timber.w("SYNC_TIMEOUT interno en medidas")
                    }
                    val job2 = async {
                        withTimeoutOrNull(2_000) {
                            val containerList = getAllContainersUseCase()
                            Timber.i("SYNC_collect de CONTAINERS en ${this@NewOrderViewModel::class.simpleName} a las ${System.currentTimeMillis()} - tamaño: ${containerList.size}")
                            _state.update { it.copy(containers = containerList) }
                        } ?: Timber.w("SYNC_TIMEOUT interno en containers")
                    }
                    val job3 = async {
                        withTimeoutOrNull(2_000) {
                            // Usar isWeekendBranch para decidir el flujo
                            if (isWeekendBranch) {
                                Timber.i("SYNC_DEBUG Antes de handleCurrentWeekOrders en [forceReload]")
                                handleCurrentWeekOrders()
                                Timber.i("SYNC_DEBUG Después de handleCurrentWeekOrders en [forceReload]")
                            } else {
                                Timber.i("SYNC_DEBUG Antes de handleLastWeekOrders en [forceReload]")
                                handleLastWeekOrders()
                                Timber.i("SYNC_DEBUG Después de handleLastWeekOrders en [forceReload]")
                            }
                        } ?: Timber.w("SYNC_TIMEOUT interno en orders")
                    }
                    awaitAll(job1, job2, job3)
                } finally {
                    Timber.i("🔥 SYNC_UI: Ocultando loader desde forceReload")
                    _state.update { it.copy(isLoading = false) }
                    Timber.i("SYNC_DEBUG isLoading puesto a false en finally [forceReload]")
                }
            }
            if (result == null) {
                Timber.e("SYNC_TIMEOUT: El ciclo principal del ViewModel ha excedido 15s. Forzando loader a false. [forceReload]")
                _state.update {
                    Timber.i("SYNC_DEBUG isLoading puesto a false tras timeout [forceReload]")
                    it.copy(isLoading = false)
                }
            }
        }
    }
}