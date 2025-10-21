package com.reguerta.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.auth.LoginUseCase
import com.reguerta.domain.time.ClockProvider
import com.reguerta.presentation.BuildConfig
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.presentation.type.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import java.time.LocalDate

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:44
 * All rights reserved 2024
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val clock: ClockProvider
) : ViewModel() {
    private var _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(newEvent: LoginEvent) {
        viewModelScope.launch {
            when (newEvent) {
                is LoginEvent.OnEmailChanged -> {
                    _state.update {
                        it.copy(
                            emailInput = newEvent.email
                        )
                    }
                    _state.update {
                        it.copy(
                            enabledButton = checkEnabledButton()
                        )
                    }
                }

                LoginEvent.OnLoginClick -> {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    loginUseCase(
                        email = state.value.emailInput,
                        password = state.value.passwordInput
                    ).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    goOut = true
                                )
                            }
                        }
                    ) { result ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message.orEmpty()
                            )
                        }
                    }
                }

                is LoginEvent.OnPasswordChanged -> {
                    _state.update {
                        it.copy(
                            passwordInput = newEvent.password
                        )
                    }
                    _state.update {
                        it.copy(
                            enabledButton = checkEnabledButton()
                        )
                    }
                }

                LoginEvent.SnackbarHide -> {
                    _state.update {
                        it.copy(
                            errorMessage = "",
                            enabledButton = false
                        )
                    }
                }
            }
        }
    }

    private fun checkEnabledButton(): Boolean {
        return with(_state.value) {
            emailInput.isValidEmail && passwordInput.isValidPassword
        }
    }

    fun autoLoginIfDebug() {
        if (BuildConfig.DEBUG) {
            val testDate = clock.today()
            viewModelScope.launch {
                val email = "ophiura@yahoo.es"
                val password = "Reguerta161274"
                try {
                    loginUseCase(email, password, testDate).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    goOut = true
                                )
                            }
                            Timber.tag("LOGIN").d("Login automático exitoso")
                        },
                        onFailure = { result ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message.orEmpty()
                                )
                            }
                            Timber.tag("LOGIN").e("Login automático fallido: ${result.message}")
                        }
                    )
                } catch (e: Exception) {
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message.orEmpty())
                    }
                    Timber.tag("LOGIN").e(e, "Excepción en login automático")
                }
            }
        }
    }
}
