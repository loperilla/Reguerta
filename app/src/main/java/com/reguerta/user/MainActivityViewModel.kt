package com.reguerta.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.AuthState
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired = _sessionExpired.asStateFlow()

    init {
        Timber.i("SYNC_MainActivityViewModel: init lanzado")
        viewModelScope.launch(Dispatchers.IO) {
            Timber.i("SYNC_MainActivityViewModel: Ejecutando refreshUser()")
            when (authService.refreshUser()) {
                is AuthState.Error -> {
                    Timber.i("SYNC_MainActivityViewModel: refreshUser -> Error (UiState.Error)")
                    _splashState.value = UiState.Error
                    _sessionExpired.value = true
                }
                AuthState.LoggedIn -> {
                    Timber.i("SYNC_MainActivityViewModel: refreshUser -> LoggedIn (UiState.Success)")
                    _splashState.value = UiState.Success
                    _sessionExpired.value = false
                }
            }
        }
    }

    fun onAppForegrounded() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Timber.i("SYNC_MainActivityViewModel: onAppForegrounded, user encontrado: ${user.email}")
                user.getIdToken(true)
                    .addOnSuccessListener {
                        Timber.i("SYNC_MainActivityViewModel: Token refreshed successfully for user: ${user.email} (UiState.Success)")
                        _splashState.value = UiState.Success
                        _sessionExpired.value = false
                    }
                    .addOnFailureListener { e ->
                        Timber.e("SYNC_MainActivityViewModel: Error refreshing token: ${e.message} (UiState.Error)")
                        _splashState.value = UiState.Error
                        _sessionExpired.value = true
                    }
            } else {
                Timber.e("SYNC_MainActivityViewModel: No user found, setting splashState to Error")
                _splashState.value = UiState.Error
                _sessionExpired.value = true
            }
        }
    }

    fun resetSessionExpired() {
        Timber.i("SYNC_MainActivityViewModel: resetSessionExpired, sessionExpired=false")
        _sessionExpired.value = false
    }
}
