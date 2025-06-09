package com.reguerta.presentation.screen.home

import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.domain.repository.ConfigCheckResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.enums.CriticalTable
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.users.SignOutUseCase
import com.reguerta.domain.usecase.week.GetCurrentWeekDayUseCase
import com.reguerta.domain.usecase.config.GetConfigUseCase
import com.reguerta.domain.usecase.products.SyncProductsUseCase
import com.reguerta.domain.usecase.containers.SyncContainersUseCase
import com.reguerta.domain.usecase.measures.SyncMeasuresUseCase
import com.reguerta.domain.usecase.orderlines.SyncOrdersAndOrderLinesUseCase
import com.reguerta.domain.usecase.app.PreloadCriticalDataUseCase
import com.reguerta.presentation.isVersionGreater
import com.reguerta.presentation.BuildConfig
import com.reguerta.presentation.getActiveCriticalTables
import com.reguerta.presentation.getTablesToSync
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

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:46
 * All rights reserved 2024
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkUserUseCase: CheckCurrentUserLoggedUseCase,
    private val getCurrentWeek: GetCurrentWeekDayUseCase,
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

    private fun checkAppState(config: ConfigModel): ConfigCheckResult {
        val androidVersion = config.versions["android"]
        val installed = BuildConfig.VERSION_NAME
        return when {
            androidVersion?.forceUpdate == true -> ConfigCheckResult.ForceUpdate
            isVersionGreater(androidVersion?.min ?: "", installed) -> ConfigCheckResult.ForceUpdate
            isVersionGreater(androidVersion?.current ?: "", installed) -> ConfigCheckResult.RecommendUpdate
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
        currentDay: DayOfWeek
    ) {
        if (!hasSyncedInSession) {
            hasSyncedInSession = true
            triggerBackgroundSync(config, isAdmin, isProducer, currentDay, skipProductSync = true)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                ForegroundSyncManager.checkAndSyncIfNeeded {
                    triggerBackgroundSync(config, isAdmin, isProducer, currentDay, skipProductSync = true)
                }
            }
        }
    }

    fun forceSync() {
        Timber.i("SYNC: forceSync lanzada a las ${System.currentTimeMillis()}")
        viewModelScope.launch(Dispatchers.IO) {
            val userResult = checkUserUseCase()
            userResult.fold(
                onSuccess = { user ->
                    // Aquí puedes llamar a la suspend function porque ya estás en una corrutina
                    val config = getConfigUseCase()
                    val currentDay = DayOfWeek.of(getCurrentWeek())
                    hasSyncedInSession = false // Permite que triggerSyncIfNeeded vuelva a sincronizar
                    _isSyncFinished.value = false
                    triggerSyncIfNeeded(
                        config,
                        user.isAdmin,
                        user.isProducer,
                        currentDay
                    )
                },
                onFailure = {
                    _state.update { it.copy(showNotAuthorizedDialog = true, isLoading = false) }
                }
            )
        }
    }

    private fun triggerBackgroundSync(
        config: ConfigModel,
        isAdmin: Boolean,
        isProducer: Boolean,
        currentDay: DayOfWeek,
        skipProductSync: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val syncMap = mutableMapOf<String, suspend (com.google.firebase.Timestamp) -> Unit>()

            if (!skipProductSync) {
                syncMap["products"] = { syncProductsUseCase(it) }
                syncMap["containers"] = { syncContainersUseCase(it) }
                syncMap["measures"] = { syncMeasuresUseCase(it) }
            }
            syncMap["orders"] = { syncOrdersAndOrderLinesUseCase(it) }

            SyncOrchestrator.runSyncIfNeeded(
                getRemoteTimestamps = { config.lastTimestamps },
                getLocalTimestamps = {
                    dataStore.getSyncTimestampsFor(
                        getActiveCriticalTables(isAdmin, isProducer, currentDay).map { it.name.lowercase() }
                    )
                },
                getCriticalTables = {
                    getActiveCriticalTables(isAdmin, isProducer, currentDay).map { it.name.lowercase() }
                },
                syncActions = syncMap
            )
            Timber.i("SYNC: triggerBackgroundSync - SyncOrchestrator terminó, se pone isSyncFinished = true en ${System.currentTimeMillis()}")
            _isSyncFinished.value = true
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
                            _state.update { it.copy(isLoading = false, configCheckResult = ConfigCheckResult.ForceUpdate) }
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
                    val currentDay = DayOfWeek.of(getCurrentWeek())
                    _state.update {
                        it.copy(
                            isCurrentUserAdmin = user.isAdmin,
                            isCurrentUserProducer = user.isProducer,
                            currentDay = currentDay,
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
                            currentDay
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
            }
        }
    }

    fun preloadCriticalDataIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            preloadCriticalDataUseCase()
        }
    }
}
