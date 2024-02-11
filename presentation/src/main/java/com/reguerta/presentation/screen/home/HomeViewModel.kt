package com.reguerta.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.firebase.auth.AuthService
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
 * From: com.reguerta.presentation.screen.home
 * Created By Manuel Lopera on 31/1/24 at 13:46
 * All rights reserved 2024
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is HomeEvent.GoOut -> {
                    authService.signOut()
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
                            showAreYouSure = false
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
