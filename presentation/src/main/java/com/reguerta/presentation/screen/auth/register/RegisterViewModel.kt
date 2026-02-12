package com.reguerta.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.auth.RegisterUseCase
import com.reguerta.domain.usecase.app.GetOrCreateDeviceIdUseCase
import com.reguerta.domain.usecase.users.UpdateUserDeviceSnapshotUseCase
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.UID_KEY
import com.reguerta.presentation.device.DeviceSnapshotFactory
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.type.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.register
 * Created By Manuel Lopera on 30/1/24 at 20:10
 * All rights reserved 2024
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val dataStore: ReguertaDataStore,
    private val getOrCreateDeviceIdUseCase: GetOrCreateDeviceIdUseCase,
    private val updateUserDeviceSnapshotUseCase: UpdateUserDeviceSnapshotUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        viewModelScope.launch {
            _state.update {
                when (event) {
                    is RegisterEvent.OnEmailChanged -> {
                        it.copy(
                            emailInput = event.email
                        )
                    }

                    is RegisterEvent.OnPasswordChanged -> {
                        it.copy(
                            passwordInput = event.password
                        )
                    }

                    is RegisterEvent.OnRepeatPasswordChanged -> {
                        it.copy(
                            repeatPasswordInput = event.repeatPassword
                        )
                    }

                    is RegisterEvent.OnRegisterClick -> {
                        it.copy(
                            enabledButton = false
                        )
                    }

                    RegisterEvent.SnackbarHide -> {
                        it.copy(
                            errorMessage = ""
                        )
                    }
                }
            }
            if (event !is RegisterEvent.OnRegisterClick) {
                _state.update {
                    it.copy(
                        enabledButton = checkEnabledButton()
                    )
                }
            } else {
                buttonWasClicked()
            }
        }
    }

    private fun buttonWasClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            registerUseCase(state.value.emailInput, state.value.passwordInput).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            goOut = true
                        )
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        reportDeviceSnapshot()
                    }
                },
                onFailure = { result ->
                    _state.update {
                        it.copy(
                            errorMessage = result.message.orEmpty()
                        )
                    }
                }
            )
        }
    }

    private fun checkEnabledButton(): Boolean {
        return with(_state.value) {
            emailInput.isValidEmail
                    && passwordInput.isValidPassword
                    && repeatPasswordInput.isValidPassword
                    && passwordInput == repeatPasswordInput
        }
    }

    private suspend fun reportDeviceSnapshot() {
        val userId = dataStore.getStringByKey(UID_KEY)
        if (userId.isBlank()) {
            return
        }
        val deviceId = getOrCreateDeviceIdUseCase()
        val snapshot = DeviceSnapshotFactory.create(deviceId)
        runCatching {
            updateUserDeviceSnapshotUseCase(userId, snapshot)
        }
    }
}
