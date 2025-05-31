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
    checkUserUseCase: CheckCurrentUserLoggedUseCase,
    getCurrentWeek: GetCurrentWeekDayUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val syncProductsUseCase: SyncProductsUseCase,
    private val syncContainersUseCase: SyncContainersUseCase,
    private val syncMeasuresUseCase: SyncMeasuresUseCase,
    private val syncOrdersAndOrderLinesUseCase: SyncOrdersAndOrderLinesUseCase,
    private val dataStore: ReguertaDataStore,
) : ViewModel() {
    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

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


    fun triggerSyncIfNeeded(config: ConfigModel, isAdmin: Boolean, isProducer: Boolean, currentDay: DayOfWeek) {
        if (!hasSyncedInSession) {
            hasSyncedInSession = true
            triggerBackgroundSync(config, isAdmin, isProducer, currentDay)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                ForegroundSyncManager.checkAndSyncIfNeeded {
                    triggerBackgroundSync(config, isAdmin, isProducer, currentDay)
                }
            }
        }
    }

    private fun triggerBackgroundSync(config: ConfigModel, isAdmin: Boolean, isProducer: Boolean, currentDay: DayOfWeek) {
        viewModelScope.launch(Dispatchers.IO) {
            SyncOrchestrator.runSyncIfNeeded(
                getRemoteTimestamps = { config.lastTimestamps },
                getLocalTimestamps = {
                    dataStore.getSyncTimestampsFor(
                        getActiveCriticalTables(isAdmin, isProducer, currentDay).map { it.name.lowercase() }
                    )
                },
                getCriticalTables = { getActiveCriticalTables(isAdmin, isProducer, currentDay).map { it.name.lowercase() } },
                syncActions = mapOf(
                    "products" to { syncProductsUseCase(it) },
                    "containers" to { syncContainersUseCase(it) },
                    "measures" to { syncMeasuresUseCase(it) },
                    "orders" to { syncOrdersAndOrderLinesUseCase(it) }
                )
            )
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val result = checkAppState(getConfigUseCase())
            _state.update { it.copy(configCheckResult = result) }

            if (result != ConfigCheckResult.Ok) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val userResult = checkUserUseCase()
            userResult.fold(
                onSuccess = { user ->
                    val currentDay = DayOfWeek.of(getCurrentWeek())
                    _state.update {
                        it.copy(
                            isCurrentUserAdmin = user.isAdmin,
                            isCurrentUserProducer = user.isProducer,
                            currentDay = currentDay,
                            isLoading = false
                        )
                    }
                    // Lanzar sincronización tras cargar el usuario
                    triggerSyncIfNeeded(
                        getConfigUseCase(),
                        user.isAdmin,
                        user.isProducer,
                        currentDay
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(showNotAuthorizedDialog = true, isLoading = false)
                    }
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
}
