package com.reguerta.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.auth.CheckCurrentUserLoggedUseCase
import com.reguerta.domain.usecase.users.SignOutUseCase
import com.reguerta.domain.usecase.week.GetCurrentWeekDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            checkUserUseCase().fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isCurrentUserAdmin = user.isAdmin,
                            isCurrentUserProducer = user.isProducer
                        )
                    }
                },
                onFailure = { result ->
                    _state.update {
                        it.copy(
                            showNotAuthorizedDialog = true
                        )
                    }
                }
            )

            _state.update {
                it.copy(
                    currentDay = DayOfWeek.of(getCurrentWeek())
                )
            }
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
