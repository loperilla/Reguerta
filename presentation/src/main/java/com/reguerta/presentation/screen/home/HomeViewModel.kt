package com.reguerta.presentation.screen.home

import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.domain.repository.ConfigCheckResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.usecase.app.PreloadCriticalDataUseCase
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.users.SignOutUseCase
import com.reguerta.domain.usecase.week.GetCurrentDayOfWeekUseCase
import com.reguerta.domain.usecase.config.GetDeliveryDayUseCase
import com.reguerta.domain.usecase.config.GetConfigUseCase
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import com.reguerta.domain.usecase.containers.SyncContainersUseCase
import com.reguerta.domain.usecase.measures.SyncMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.SyncOrdersAndOrderLinesUseCase
import com.reguerta.domain.usecase.users.SyncUsersUseCase
import com.reguerta.presentation.isVersionGreater
import com.reguerta.presentation.BuildConfig
import com.reguerta.presentation.getActiveCriticalTables
import com.reguerta.presentation.sync.SyncOrchestrator
import com.reguerta.presentation.sync.ForegroundSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.supervisorScope
import java.util.Locale

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:46
 * All rights reserved 2024
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkUserUseCase: CheckCurrentUserLoggedUseCase,
    private val getCurrentDayOfWeekUseCase: GetCurrentDayOfWeekUseCase,
    private val getDeliveryDayUseCase: GetDeliveryDayUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val syncContainersUseCase: SyncContainersUseCase,
    private val syncMeasuresUseCase: SyncMeasuresUseCase,
    private val syncOrdersAndOrderLinesUseCase: SyncOrdersAndOrderLinesUseCase,
    private val syncUsersUseCase: SyncUsersUseCase,
    private val dataStore: ReguertaDataStore,
    private val preloadCriticalDataUseCase: PreloadCriticalDataUseCase,
) : ViewModel() {
    private val _isSyncFinished = MutableStateFlow(false)
    val isSyncFinished: StateFlow<Boolean> = _isSyncFinished.asStateFlow()

    private val _canOpenOrders = MutableStateFlow(false)
    val canOpenOrders: StateFlow<Boolean> = _canOpenOrders.asStateFlow()

    private val _isCheckingOrders = MutableStateFlow(false)
    val isCheckingOrders: StateFlow<Boolean> = _isCheckingOrders.asStateFlow()

    private val _openOrders = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openOrders = _openOrders.asSharedFlow()

    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    val isFirstRun: StateFlow<Boolean> = dataStore.isFirstRun.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    private var hasSyncedInSession = false

    private var cachedConfig: ConfigModel? = null

    /**
     * Intenta obtener una config fresca desde Firestore. Si falla, vuelve a la cachedConfig.
     * Importante para el caso de apps que quedan días en segundo plano con el proceso vivo.
     */
    private suspend fun getLatestConfigOrCached(): ConfigModel? {
        val fresh = runCatching { getConfigUseCase() }
            .onFailure { Timber.tag("FOREGROUND").w(it, "getConfigUseCase() falló; usando cachedConfig si existe") }
            .getOrNull()

        if (fresh != null) {
            cachedConfig = fresh
            return fresh
        }
        return cachedConfig
    }

    private fun List<String>.toRootLower(): List<String> = this.map { it.lowercase(Locale.ROOT) }

    private fun checkAppState(config: ConfigModel): ConfigCheckResult {
        val androidVersion = config.versions["android"]
        val installed = BuildConfig.VERSION_NAME
        val min = androidVersion?.min ?: ""
        val current = androidVersion?.current ?: ""
        val force = androidVersion?.forceUpdate ?: false

        return when {
            force -> ConfigCheckResult.ForceUpdate
            isVersionGreater(min, installed) -> ConfigCheckResult.ForceUpdate
            isVersionGreater(current, installed) -> ConfigCheckResult.RecommendUpdate
            else -> ConfigCheckResult.Ok
        }
    }

    fun setFirstRunFalse() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.setIsFirstRun(false)
        }
    }


    /** Verifica si todas las tablas críticas requeridas para el rol/día están sincronizadas respecto a los lastTimestamps remotos (>=). */
    private suspend fun areCriticalTablesFresh(
        config: ConfigModel,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        deliveryDay: WeekDay
    ): Boolean {
        val required = getActiveCriticalTables(isProducer, currentDay, deliveryDay).map { it.name.lowercase(Locale.ROOT) }
        Timber.tag("SYNC_FRESH").d("required keys=%s", required)
        if (required.isEmpty()) return true

        val remote: Map<String, Timestamp> = config.lastTimestamps
        Timber.tag("SYNC_FRESH").d("remote keys=%s", remote.keys)
        val local: Map<String, Long> = dataStore.getSyncTimestampsFor(required)
        Timber.tag("SYNC_FRESH").d("local keys=%s", local.keys)

        var allFresh = true
        required.forEach { key ->
            val rSec: Long? = remote[key]?.seconds
            val lSec: Long? = local[key] // DataStore guarda epochSeconds
            val deltaSec = if (lSec != null && rSec != null) (lSec - rSec) else null
            Timber.tag("SYNC_FRESH").d(
                "key=%s localSec=%s remoteSec=%s deltaSec=%s",
                key, lSec?.toString() ?: "-", rSec?.toString() ?: "-", deltaSec?.toString() ?: "-"
            )
            val fresh = when {
                rSec == null -> true     // sin remoto no bloquea
                lSec == null -> false    // falta local → no fresco
                else -> lSec >= rSec     // compara en segundos
            }
            if (!fresh) allFresh = false
        }
        Timber.tag("SYNC_FRESH").i("allFresh=%s for required=%s", allFresh, required)
        return allFresh
    }

    fun triggerSyncIfNeeded(
        config: ConfigModel,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        deliveryDay: WeekDay
    ) {
        if (!hasSyncedInSession) {
            Timber.tag("SYNC_BG").i("first session sync → triggerBackgroundSync")
            hasSyncedInSession = true
            viewModelScope.launch(Dispatchers.IO) {
                triggerBackgroundSync(config, isProducer, currentDay, deliveryDay)
            }
        } else {
            Timber.tag("SYNC_BG").i("foreground path → ForegroundSyncManager.checkAndSyncIfNeeded")
            viewModelScope.launch(Dispatchers.IO) {
                ForegroundSyncManager.checkAndSyncIfNeeded {
                    triggerBackgroundSync(config, isProducer, currentDay, deliveryDay)
                }
            }
        }
    }

    /** Llamar al volver a primer plano (ON_START) de la pantalla raíz. */
    fun onAppForegrounded() {
        Timber.tag("FOREGROUND").i("HomeVM.onAppForegrounded()")
        viewModelScope.launch(Dispatchers.IO) {
            ForegroundSyncManager.checkAndSyncIfNeeded {
                val cfg = getLatestConfigOrCached() ?: run {
                    Timber.tag("FOREGROUND").w("onAppForegrounded(): config=null (fresh+cached), skip")
                    return@checkAndSyncIfNeeded
                }
                val s = state.value
                Timber.tag("FOREGROUND").i("foreground sync with fresh config → triggerBackgroundSync")
                triggerBackgroundSync(
                    config = cfg,
                    isProducer = s.isCurrentUserProducer,
                    currentDay = s.currentDay,
                    deliveryDay = s.deliveryDay
                )
            }
        }
    }

    private suspend fun triggerBackgroundSync(
        config: ConfigModel,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        deliveryDay: WeekDay
    ) {
        // Pre‑sync snapshot (antes incluso de la verificación de frescura)
        val criticalPre = getActiveCriticalTables(isProducer, currentDay, deliveryDay)
            .map { it.name }
            .toRootLower()
        val localPre = dataStore.getSyncTimestampsFor(criticalPre)
        Timber.tag("SYNC_BG").d("critical(pre)=%s", criticalPre)
        Timber.tag("SYNC_BG").d("localTs(pre)=%s", localPre)
        Timber.tag("SYNC_BG").d("remoteTs(keys)=%s", config.lastTimestamps.keys)

        // Pre‑check: si todas ya están frescas, no disparamos sync ni tocamos el botón
        val alreadyFresh = areCriticalTablesFresh(
            config = config,
            isProducer = isProducer,
            currentDay = currentDay,
            deliveryDay = deliveryDay
        )
        if (alreadyFresh) {
            Timber.tag("SYNC_BG").d("Skip sync: data already fresh → keeping 'Mi pedido' enabled")
            _canOpenOrders.value = true
            _isSyncFinished.value = true
            _isCheckingOrders.value = false
            return
        }

        // Solo aquí, si NO está fresco, deshabilitamos temporalmente el botón y marcamos sync en curso
        _canOpenOrders.value = false
        _isSyncFinished.value = false
        try {
            val syncMap = mutableMapOf<String, suspend (Timestamp) -> Unit>()
            syncMap["products"] = { syncProductsUseCase(it) }
            syncMap["containers"] = { syncContainersUseCase(it) }
            syncMap["measures"] = { syncMeasuresUseCase(it) }
            syncMap["orders"] = { syncOrdersAndOrderLinesUseCase(it) }
            syncMap["users"] = { syncUsersUseCase(it) }
            Timber.tag("SYNC_BG").d("syncMap keys=%s", syncMap.keys)

            // Claves críticas activas, normalizadas y filtradas a las acciones disponibles
            val critical = getActiveCriticalTables(isProducer, currentDay, deliveryDay)
                .map { it.name }
                .toRootLower()
                .filter { it in syncMap.keys }
            Timber.tag("SYNC_BG").d("critical required to sync=%s", critical)
            Timber.tag("SYNC_BG").d("remote timestamp keys=%s", config.lastTimestamps.keys)

            supervisorScope {
                SyncOrchestrator.runSyncIfNeeded(
                    getRemoteTimestamps = { config.lastTimestamps },
                    getLocalTimestamps = { dataStore.getSyncTimestampsFor(critical) },
                    getCriticalTables = { critical },
                    syncActions = syncMap
                )
            }
            val localPost = dataStore.getSyncTimestampsFor(critical)
            Timber.tag("SYNC_BG").d("localTs(post)=%s", localPost)
            // Re-evaluar si ya podemos abrir Mi Pedido con seguridad
            _canOpenOrders.value = areCriticalTablesFresh(
                config = config,
                isProducer = isProducer,
                currentDay = currentDay,
                deliveryDay = deliveryDay
            )
            Timber.tag("SYNC_BG").i("canOpenOrders computed=%s", _canOpenOrders.value)
            Timber.i("SYNC: triggerBackgroundSync - SyncOrchestrator terminó OK @${System.currentTimeMillis()}")
        } catch (t: Throwable) {
            _canOpenOrders.value = false
            Timber.e(t, "SYNC: triggerBackgroundSync falló")
        } finally {
            _isSyncFinished.value = true // nunca dejamos el loader inicial enganchado
            _isCheckingOrders.value = false
        }
    }

    // INIT: Al iniciar, carga usuario y configuración, controla errores y actualiza el estado atómicamente.
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Siempre marcamos como cargando y ocultamos el diálogo de no autorizado al entrar
            _state.update { it.copy(isLoading = true, showNotAuthorizedDialog = false) }

            // Paso 1: Comprobar usuario
            val userResult = checkUserUseCase()
            userResult.fold(
                onSuccess = { user ->
                    // Paso 2: Obtener configuración de forma segura
                    val configResult = runCatching { getConfigUseCase() }
                    val result = configResult.fold(
                        onSuccess = { config ->
                            // Comprobar el estado de la app según la config
                            val checkResult = checkAppState(config)
                            cachedConfig = config
                            checkResult
                        },
                        onFailure = {
                            // Si falla la config, forzar actualización y mostrar error
                            _state.update { homeState -> homeState.copy(isLoading = false, configCheckResult = ConfigCheckResult.ForceUpdate) }
                            Timber.e(it, "Error al obtener configuración, se fuerza actualización")
                            return@launch
                        }
                    )
                    // Actualizar el resultado de comprobación de config
                    _state.update { it.copy(configCheckResult = result) }
                    // Si la configuración requiere forzar actualización, no continuar
                    if (result != ConfigCheckResult.Ok) {
                        _state.update { it.copy(isLoading = false) }
                        return@launch
                    }
                    // Paso 3: Usuario y configuración correctos, obtener día actual
                    val currentDay = runCatching { getCurrentDayOfWeekUseCase() }
                        .getOrElse { DayOfWeek.from(java.time.LocalDate.now()) }
                    val deliveryDay = runCatching { getDeliveryDayUseCase() }
                        .getOrElse { WeekDay.WED }
                    _state.update {
                        it.copy(
                            isCurrentUserAdmin = user.isAdmin,
                            isCurrentUserProducer = user.isProducer,
                            currentDay = currentDay,
                            deliveryDay = deliveryDay,
                            isLoading = false
                        )
                    }
                    // Solo lanzar sincronización si la config es válida
                    if (result == ConfigCheckResult.Ok) {
                        // Reutilizamos la config ya obtenida o la recuperamos de forma segura
                        val cfg = configResult.getOrNull() ?: getConfigUseCase()

                        // Si faltan datos críticos locales (timestamps en segundos < remotos),
                        // ejecutamos un preload inicial que trae todas las tablas críticas y sella timestamps.
                        val needsPreload = !areCriticalTablesFresh(
                            config = cfg,
                            isProducer = user.isProducer,
                            currentDay = currentDay,
                            deliveryDay = deliveryDay
                        )
                        if (needsPreload) {
                            Timber.tag("SYNC_Preload").i("Faltan datos críticos locales → lanzando PreloadCriticalDataUseCase")
                            runCatching { preloadCriticalDataUseCase() }
                                .onSuccess { Timber.tag("SYNC_Preload").i("Preload completado") }
                                .onFailure { Timber.w(it, "Preload falló (no bloqueante)") }
                        }

                        // Si ya estaban todas frescas y no hemos necesitado preload, marcamos el primer arranque como completado
                        if (!needsPreload) {
                            _isSyncFinished.value = true
                            _canOpenOrders.value = true
                            setFirstRunFalse()
                        }
                        // Lanzar sincronización tras (posible) preload
                        triggerSyncIfNeeded(
                            cfg,
                            user.isProducer,
                            currentDay,
                            deliveryDay
                        )
                    }
                    // Si no, el flujo termina aquí y no se queda en loading
                },
                onFailure = {
                    // Si falla usuario, mostrar diálogo y quitar loading
                    _state.update { it.copy(showNotAuthorizedDialog = true, isLoading = false) }
                    return@launch
                }
            )
        }
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is HomeEvent.GoOut -> {
                    signOutUseCase()
                    _state.update {
                        it.copy(
                            goOut = true,
                            showAreYouSure = false
                        )
                    }
                }

                HomeEvent.HideDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSure = false,
                            configCheckResult = null
                        )
                    }
                }

                HomeEvent.ShowDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSure = true
                        )
                    }
                }

                HomeEvent.ShowBlockedDayDialog -> {
                    _state.update {
                        it.copy(
                            showBlockedDayDialog = true
                        )
                    }
                }

                HomeEvent.HideBlockedDayDialog -> {
                    _state.update {
                        it.copy(
                            showBlockedDayDialog = false
                        )
                    }
                }
            }
        }
    }


    /**
     * Re-verifica frescura justo antes de abrir "Mi Pedido".
     * Si no está fresco, lanza un sync en foreground y reevalúa canOpenOrders.
     */
    fun recheckAndSyncBeforeOpenOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            _isCheckingOrders.value = true
            Timber.tag("SYNC_ONCLICK").d("recheck start with cachedConfig=%s", (cachedConfig != null))
            val cfg = getLatestConfigOrCached() ?: run {
                _isCheckingOrders.value = false
                Timber.tag("SYNC_ONCLICK").w("recheck: config=null (fresh+cached), abort")
                return@launch
            }
            val s = state.value
            val fresh = areCriticalTablesFresh(
                config = cfg,
                isProducer = s.isCurrentUserProducer,
                currentDay = s.currentDay,
                deliveryDay = s.deliveryDay
            )
            Timber.tag("SYNC_ONCLICK").d("fresh=%s (before foreground sync)", fresh)
            if (!fresh) {
                Timber.tag("SYNC_ONCLICK").i("not fresh → triggering ForegroundSyncManager")
                _canOpenOrders.value = false
                ForegroundSyncManager.checkAndSyncIfNeeded {
                    triggerBackgroundSync(
                        config = cfg,
                        isProducer = s.isCurrentUserProducer,
                        currentDay = s.currentDay,
                        deliveryDay = s.deliveryDay
                    )
                }
            } else {
                _isCheckingOrders.value = false
                _canOpenOrders.value = true
                Timber.tag("SYNC_ONCLICK").i("fresh → emitting openOrders")
                _openOrders.tryEmit(Unit)
            }
        }
    }
}
