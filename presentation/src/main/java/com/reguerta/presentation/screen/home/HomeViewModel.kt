package com.reguerta.presentation.screen.home

import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.domain.repository.ConfigCheckResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.enums.WeekDay
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.users.SignOutUseCase
import com.reguerta.domain.usecase.week.GetCurrentDayOfWeekUseCase
import com.reguerta.domain.usecase.config.GetDeliveryDayUseCase
import com.reguerta.domain.usecase.config.GetConfigUseCase
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import com.reguerta.domain.usecase.containers.SyncContainersUseCase
import com.reguerta.domain.usecase.measures.SyncMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.SyncOrdersAndOrderLinesUseCase
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
    private val dataStore: ReguertaDataStore,
    private val preloadCriticalDataUseCase: com.reguerta.domain.usecase.app.PreloadCriticalDataUseCase,
) : ViewModel() {
    private val _isSyncFinished = MutableStateFlow(false)
    val isSyncFinished: StateFlow<Boolean> = _isSyncFinished.asStateFlow()

    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    val isFirstRun: StateFlow<Boolean> = dataStore.isFirstRun.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    private var hasSyncedInSession = false

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


    fun triggerSyncIfNeeded(
        config: ConfigModel,
        isAdmin: Boolean,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        deliveryDay: WeekDay
    ) {
        val skipProducts = !isFirstRun.value // en primer inicio NO saltamos productos
        if (!hasSyncedInSession) {
            hasSyncedInSession = true
            triggerBackgroundSync(config, isAdmin, isProducer, currentDay, deliveryDay, skipProductSync = skipProducts)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                ForegroundSyncManager.checkAndSyncIfNeeded {
                    triggerBackgroundSync(config, isAdmin, isProducer, currentDay, deliveryDay, skipProductSync = skipProducts)
                }
            }
        }
    }

    private fun triggerBackgroundSync(
        config: ConfigModel,
        isAdmin: Boolean,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        deliveryDay: WeekDay,
        skipProductSync: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncFinished.value = false
            try {
                val syncMap = mutableMapOf<String, suspend (com.google.firebase.Timestamp) -> Unit>()

                if (!skipProductSync) {
                    syncMap["products"] = { syncProductsUseCase(it) }
                    syncMap["containers"] = { syncContainersUseCase(it) }
                    syncMap["measures"] = { syncMeasuresUseCase(it) }
                }
                syncMap["orders"] = { syncOrdersAndOrderLinesUseCase(it) }

                // Claves críticas activas, normalizadas y filtradas a las acciones disponibles
                val critical = getActiveCriticalTables(isAdmin, isProducer, currentDay, deliveryDay)
                    .map { it.name }
                    .toRootLower()
                    .filter { it in syncMap.keys }

                supervisorScope {
                    SyncOrchestrator.runSyncIfNeeded(
                        getRemoteTimestamps = { config.lastTimestamps },
                        getLocalTimestamps = { dataStore.getSyncTimestampsFor(critical) },
                        getCriticalTables = { critical },
                        syncActions = syncMap
                    )
                }

                Timber.i("SYNC: triggerBackgroundSync - SyncOrchestrator terminó OK @${System.currentTimeMillis()}")
            } catch (t: Throwable) {
                Timber.e(t, "SYNC: triggerBackgroundSync falló")
            } finally {
                _isSyncFinished.value = true // nunca dejamos el loader inicial enganchado
            }
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
                            checkAppState(config)
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
                        _isSyncFinished.value = false
                        // Lanzar sincronización tras cargar el usuario y config
                        triggerSyncIfNeeded(
                            configResult.getOrNull() ?: getConfigUseCase(),
                            user.isAdmin,
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

    fun preloadCriticalDataIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { preloadCriticalDataUseCase() }
                .onFailure { Timber.w(it, "Preload falló (no bloqueante)") }
        }
    }
}
