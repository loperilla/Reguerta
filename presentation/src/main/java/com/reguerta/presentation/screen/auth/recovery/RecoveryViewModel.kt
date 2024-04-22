package com.reguerta.presentation.screen.auth.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.auth.SendRecoveryPasswordEmailUseCase
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
 * From: com.reguerta.presentation.screen.auth.recovery
 * Created By Manuel Lopera on 7/4/24 at 15:54
 * All rights reserved 2024
 */
@HiltViewModel
class RecoveryViewModel @Inject constructor(
    private val recoveryPasswordEmailUseCase: SendRecoveryPasswordEmailUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<RecoveryPasswordState> = MutableStateFlow(RecoveryPasswordState())
    val state: StateFlow<RecoveryPasswordState> = _state.asStateFlow()


    fun onEvent(event: RecoveryEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                RecoveryEvent.SendEmail -> {
                    recoveryPasswordEmailUseCase(state.value.email).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(showSuccessDialog = true)
                            }
                        },
                        onFailure = {
                            _state.update {
                                it.copy(showFailureDialog = true)
                            }
                        }
                    )
                }

                RecoveryEvent.GoBack -> {
                    _state.update {
                        it.copy(goOut = true)
                    }
                }
                is RecoveryEvent.EmailChanged -> {
                    _state.update {
                        it.copy(email = event.email)
                    }
                }

                RecoveryEvent.HideFailureDialog -> {
                    _state.update {
                        it.copy(
                            showFailureDialog = false,
                            email = ""
                        )
                    }
                }
            }
        }
    }
}