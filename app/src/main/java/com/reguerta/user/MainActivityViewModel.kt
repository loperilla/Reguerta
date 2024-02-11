package com.reguerta.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.AuthState
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.user
 * Created By Manuel Lopera on 23/1/24 at 20:36
 * All rights reserved 2024
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authService: AuthService
) : ViewModel() {
    private var _splashState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val splashState: StateFlow<UiState> = _splashState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _splashState.value = when (authService.refreshUser()) {
                is AuthState.Error -> UiState.Error
                AuthState.LoggedIn -> UiState.Success
            }
        }
    }
}
