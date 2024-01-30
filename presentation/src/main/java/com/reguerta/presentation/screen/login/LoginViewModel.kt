package com.reguerta.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.AuthState
import com.reguerta.data.firebase.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.login
 * Created By Manuel Lopera on 24/1/24 at 19:44
 * All rights reserved 2024
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private var _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(newEvent: LoginEvent) {
        viewModelScope.launch {
            when (newEvent) {
                is LoginEvent.OnEmailChanged -> _state.update {
                    it.copy(
                        emailInput = newEvent.email
                    )
                }

                LoginEvent.OnLoginClick -> {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }

                    when (val result = authService.logInWithUserPassword(
                        email = state.value.emailInput,
                        password = state.value.passwordInput
                    )) {
                        is AuthState.Error -> TODO()
                        AuthState.LoggedIn -> TODO()
                    }
                }

                is LoginEvent.OnPasswordChanged -> _state.update {
                    it.copy(
                        passwordInput = newEvent.password
                    )
                }
            }
        }
    }
}
